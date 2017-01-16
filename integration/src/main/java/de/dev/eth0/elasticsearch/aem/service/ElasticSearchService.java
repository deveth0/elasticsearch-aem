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
package de.dev.eth0.elasticsearch.aem.service;

import java.io.IOException;
import lombok.Getter;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service keeping a connection to the ElasticSearch host
 */
@Component(metatype = false, immediate = true)
@Service(ElasticSearchService.class)
public class ElasticSearchService {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchHostConfiguration.class);
  // TODO: support multiple hosts
  @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
  protected ElasticSearchHostConfiguration hostConfiguration;

  @Getter
  private RestClient restClient;

  @Activate
  public void activate(ComponentContext context) {
    restClient = RestClient.builder(new HttpHost(hostConfiguration.getHost(), hostConfiguration.getPort(), hostConfiguration.getProtocol())).build();
  }

  @Deactivate
  public void deactivate(ComponentContext context) {
    if (this.restClient != null) {
      try {
        this.restClient.close();
      }
      catch (IOException ioe) {
        LOG.warn("Could not close ElasticSearch RestClient", ioe);
      }
    }
  }

}
