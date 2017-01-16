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

import lombok.Getter;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

/**
 * Configuration for a single ElasticSearch host
 */
@Component(metatype = true, immediate = true,
        label = ElasticSearchHostConfiguration.SERVICE_NAME, description = ElasticSearchHostConfiguration.SERVICE_DESCRIPTION)
@Service(ElasticSearchHostConfiguration.class)
@Properties({
  @Property(name = "webconsole.configurationFactory.nameHint", value = "Host: {host}")
})
public class ElasticSearchHostConfiguration {


  public static final String SERVICE_NAME = "ElasticSearch Search Provider";
  public static final String SERVICE_DESCRIPTION = "Configuration for the ElasticSearch Search Provider";

  @Property(name = "host",
          label = "Hostname",
          value = "localhost",
          description = "Hostname of the ElasticSearch")
  public static final String PROPERTY_HOST = "host";

  private static final int PROPERTY_PORT_DEFAULT = 9200;
  @Property(name = "port",
          label = "Port",
          intValue = PROPERTY_PORT_DEFAULT,
          description = "Port of the ElasticSearch (default: 9200)."
  )
  public static final String PROPERTY_PORT = "port";

  private static final String PROPERTY_PROTOCOL_DEFAULT = "http";
  @Property(name = "protocol",
          label = "Protocol",
          description = "Protocol for Elasticsearch",
          value = PROPERTY_PROTOCOL_DEFAULT,
          options = {
            @PropertyOption(name = "http", value = "http")
            ,
      @PropertyOption(name = "https", value = "https")
          })
  public static final String PROPERTY_PROTOCOL = "protocol";

  @Getter
  protected String protocol;
  @Getter
  protected String host;
  @Getter
  protected int port;

  protected ComponentContext context;

  @Activate
  public void activate(ComponentContext context) {
    this.context = context;
    this.protocol = PropertiesUtil.toString(context.getProperties().get(ElasticSearchHostConfiguration.PROPERTY_PROTOCOL), ElasticSearchHostConfiguration.PROPERTY_PROTOCOL_DEFAULT);
    this.host = PropertiesUtil.toString(context.getProperties().get(ElasticSearchHostConfiguration.PROPERTY_HOST), null);
    this.port = PropertiesUtil.toInteger(context.getProperties().get(ElasticSearchHostConfiguration.PROPERTY_PORT), ElasticSearchHostConfiguration.PROPERTY_PORT_DEFAULT);
  }


}
