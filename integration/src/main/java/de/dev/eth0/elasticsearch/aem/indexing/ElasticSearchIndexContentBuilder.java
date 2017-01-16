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

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ContentBuilder;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationContent;
import com.day.cq.replication.ReplicationContentFactory;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dev.eth0.elasticsearch.aem.indexing.config.ElasticSearchIndexConfiguration;
import de.dev.eth0.elasticsearch.aem.indexing.contentbuilder.ElasticSearchContentBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * This Content-Builder is responsible to filter all relevant information from the activation event into a ReplicationContent which is processed by
 * {@link ElasticSearchTransportHandler}.
 */
@Component(metatype = false)
@Service(ContentBuilder.class)
@Property(name = "name", value = ElasticSearchIndexContentBuilder.NAME)
public class ElasticSearchIndexContentBuilder implements ContentBuilder {

  private BundleContext context;

  @Reference
  private ResourceResolverFactory resolverFactory;

  /**
   * Name of the Content Builder
   */
  public static final String NAME = "elastic";
  /**
   * Title of the Content Builder
   */
  public static final String TITLE = "Elastic Search Index Content";

  @Activate
  public void activate(BundleContext context) {
    this.context = context;
  }

  @Override
  public ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory factory) throws ReplicationException {
    return create(session, action, factory, null);
  }

  @Override
  public ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory factory, Map<String, Object> map) throws ReplicationException {
    String path = action.getPath();
    ReplicationLog log = action.getLog();

    if (StringUtils.isNotBlank(path)) {
      try {
        HashMap<String, Object> sessionMap = new HashMap<>();
        sessionMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
        ResourceResolver resolver = resolverFactory.getResourceResolver(sessionMap);

        Resource resource = resolver.getResource(path);
        if (resource != null) {
          String primaryType = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
          ElasticSearchContentBuilder builder = getContentBuilder(primaryType, log);
          if (builder != null) {
            return createReplicationContent(factory, builder.create(path, resolver));
          }
        }
      }
      catch (LoginException e) {
        log.error("Could not retrieve Page Manager", e);
      }

    }
    log.info(getClass().getSimpleName() + ": Path is blank");
    return ReplicationContent.VOID;
  }

  /**
   * Looks up a ContentBuilder implementation for the given PrimaryType.
   *
   * @param primaryType
   * @param log
   * @return ElasticSearchIndexConfiguration or null if none found
   */
  private ElasticSearchContentBuilder getContentBuilder(String primaryType, ReplicationLog log) {
    try {
      ServiceReference[] serviceReferences = context.getServiceReferences(ElasticSearchContentBuilder.class.getName(),
              "(" + ElasticSearchIndexConfiguration.PRIMARY_TYPE + "=" + primaryType + ")");
      if (serviceReferences != null && serviceReferences.length > 0) {
        return (ElasticSearchContentBuilder)context.getService(serviceReferences[0]);
      }
    }
    catch (InvalidSyntaxException | NullPointerException ex) {
      log.info(getClass().getSimpleName() + ": Could not load a ElasticSearchContentBuilder for PrimaryType " + primaryType);
    }
    return null;
  }

  private ReplicationContent createReplicationContent(ReplicationContentFactory factory, IndexEntry content) throws ReplicationException {
    Path tempFile;

    try {
      tempFile = Files.createTempFile("elastic_index", ".tmp");
    }
    catch (IOException e) {
      throw new ReplicationException("Could not create temporary file", e);
    }

    try (BufferedWriter writer = Files.newBufferedWriter(tempFile, Charset.forName("UTF-8"))) {
      ObjectMapper mapper = new ObjectMapper();
      writer.write(mapper.writeValueAsString(content));
      writer.flush();

      return factory.create("text/plain", tempFile.toFile(), true);
    }
    catch (IOException e) {
      throw new ReplicationException("Could not write to temporary file", e);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getTitle() {
    return TITLE;
  }

}
