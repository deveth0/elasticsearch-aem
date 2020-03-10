/*
 * #%L
 * dev-eth0.de
 * %%
 * Copyright (C) 2016 dev-eth0.de
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.dev.eth0.elasticsearch.aem.indexing;

import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationContent;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.TransportContext;
import com.day.cq.replication.TransportHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dev.eth0.elasticsearch.aem.service.ElasticSearchService;
import java.io.IOException;
import java.util.Collections;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.sling.commons.json.JSONException;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransportHandler Implementation that posts a ReplicationContent created by {@link ElasticSearchIndexContentBuilder} to a configured ElasticSearch.
 */
@Service(TransportHandler.class)
@Component(label = "Elastic Search Index Agent", immediate = true)
@Properties(
        @Property(name = Constants.SERVICE_RANKING, intValue = 1000))
public class ElasticSearchTransportHandler implements TransportHandler {

  @Reference
  protected ElasticSearchService elasticSearchService;

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchTransportHandler.class);

  /**
   *
   * @param config
   * @return only accept if the serializationType is "elastic"
   */
  @Override
  public boolean canHandle(AgentConfig config) {
    return StringUtils.equalsIgnoreCase(config.getSerializationType(), ElasticSearchIndexContentBuilder.NAME);
  }

  /**
   *
   * @param ctx
   * @param tx
   * @return
   * @throws ReplicationException
   */
  @Override
  public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx) throws ReplicationException {
    ReplicationLog log = tx.getLog();
    try {
      RestClient restClient = elasticSearchService.getRestClient();
      ReplicationActionType replicationType = tx.getAction().getType();
      if (replicationType == ReplicationActionType.TEST) {
        return doTest(ctx, tx, restClient);
      }
      else {
        log.info(getClass().getSimpleName() + ": ---------------------------------------");
        if (tx.getContent() == ReplicationContent.VOID) {
          LOG.warn("No Replication Content provided");
          return new ReplicationResult(true, 0, "No Replication Content provided for path " + tx.getAction().getPath());
        }
        switch (replicationType) {
          case ACTIVATE:
            return doActivate(ctx, tx, restClient);
          case DEACTIVATE:
            return doDeactivate(ctx, tx, restClient);
          default:
            log.warn(getClass().getSimpleName() + ": Replication action type" + replicationType + " not supported.");
            throw new ReplicationException("Replication action type " + replicationType + " not supported.");
        }
      }
    }
    catch (JSONException jex) {
      LOG.error("JSON was invalid", jex);
      return new ReplicationResult(false, 0, jex.getLocalizedMessage());
    }
    catch (IOException ioe) {
      log.error(getClass().getSimpleName() + ": Could not perform Indexing due to " + ioe.getLocalizedMessage());
      LOG.error("Could not perform Indexing", ioe);
      return new ReplicationResult(false, 0, ioe.getLocalizedMessage());
    }
  }

  private ReplicationResult doDeactivate(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws ReplicationException, JSONException, IOException {
    ReplicationLog log = tx.getLog();

    ObjectMapper mapper = new ObjectMapper();
    IndexEntry content = mapper.readValue(tx.getContent().getInputStream(), IndexEntry.class);
    Response deleteResponse = restClient.performRequest(
            "DELETE",
            "/" + content.getIndex() + "/" + content.getType() + "/" + DigestUtils.md5Hex(content.getPath()),
            Collections.<String, String>emptyMap());
    LOG.debug(deleteResponse.toString());
    log.info(getClass().getSimpleName() + ": Delete Call returned " + deleteResponse.getStatusLine().getStatusCode() + ": " + deleteResponse.getStatusLine().getReasonPhrase());
    if (deleteResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || deleteResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return ReplicationResult.OK;
    }
    LOG.error("Could not delete " + content.getType() + " at " + content.getPath());
    return new ReplicationResult(false, 0, "Replication failed");
  }

  /**
   * Perform the replication. All logic is covered in {@link ElasticSearchIndexContentBuilder} so we only need to transmit the JSON to ElasticSearch
   *
   * @param ctx
   * @param tx
   * @param restClient
   * @return
   * @throws ReplicationException
   */
  private ReplicationResult doActivate(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws ReplicationException, JSONException, IOException {
    ReplicationLog log = tx.getLog();
    ObjectMapper mapper = new ObjectMapper();
    IndexEntry content = mapper.readValue(tx.getContent().getInputStream(), IndexEntry.class);
    if (content != null) {
      log.info(getClass().getSimpleName() + ": Indexing " + content.getPath());
      String contentString = mapper.writeValueAsString(content.getContent());
      log.debug(getClass().getSimpleName() + ": Index-Content: " + contentString);
      LOG.debug("Index-Content: " + contentString);

      HttpEntity entity = new NStringEntity(contentString, ContentType.APPLICATION_JSON);
      Response indexResponse = restClient.performRequest(
              "PUT",
              "/" + content.getIndex() + "/" + content.getType() + "/" + DigestUtils.md5Hex(content.getPath()),
              Collections.<String, String>emptyMap(),
              entity);
      LOG.debug(indexResponse.toString());
      log.info(getClass().getSimpleName() + ": " + indexResponse.getStatusLine().getStatusCode() + ": " + indexResponse.getStatusLine().getReasonPhrase());
      if (indexResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || indexResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        return ReplicationResult.OK;
      }
    }
    LOG.error("Could not replicate");
    return new ReplicationResult(false, 0, "Replication failed");
  }

  /**
   * Test Connection to ElasticSearch by getting basic informations
   *
   * @param ctx
   * @param tx
   * @param restClient
   * @return
   * @throws ReplicationException
   */
  private ReplicationResult doTest(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws ReplicationException, IOException {
    ReplicationLog log = tx.getLog();
    Response response = restClient.performRequest("GET", "/", Collections.singletonMap("pretty", "true"));
    log.info(getClass().getSimpleName() + ": ---------------------------------------");
    log.info(getClass().getSimpleName() + ": " + response.toString());
    log.info(getClass().getSimpleName() + ": " + EntityUtils.toString(response.getEntity()));
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return ReplicationResult.OK;
    }
    return new ReplicationResult(false, 0, "Replication test failed");
  }

}
