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
package de.dev.eth0.elasticsearch.aem.indexing.config;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, immediate = true, configurationFactory = true,
        label = ElasticSearchIndexConfiguration.SERVICE_NAME, description = ElasticSearchIndexConfiguration.SERVICE_DESCRIPTION)
@Service(ElasticSearchIndexConfiguration.class)
@Properties({
  @Property(name = "webconsole.configurationFactory.nameHint", value = "Primary Type: {primaryType}")
})
public class ElasticSearchIndexConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchIndexConfiguration.class);
//TODO: add additional filter for PATH

  /**
   * Filter Property for jcr:primary Type
   */
  public static final String PRIMARY_TYPE = JcrConstants.JCR_PRIMARYTYPE;

  public static final String SERVICE_NAME = "ElasticSearch Index Configuration";
  public static final String SERVICE_DESCRIPTION = "Service to configure the elasticSearch Index";

  @Property(name = "primaryType",
          label = "Primary Type",
          description = "Primary Type for which this configuration is responsible. E.g. cq:page or dam:Asset")
  public static final String PROPERTY_BASE_PATH = "primaryType";

  //TODO: add support for mapping hints (e.g. jcr:title;keyword
  @Property(name = "indexRules",
          cardinality = Integer.MAX_VALUE,
          label = "Index Rules",
          description = "List with the names of all properties that should be indexed."
  )
  public static final String PROPERTY_INDEX_RULES = "indexRules";

  @Property(name = "reindex",
          label = "Reindex",
          boolValue = false,
          description = "If enabled, a reindexing will start on save")
  public static final String PROPERTY_REINDEX = "reindex";

  protected String[] indexRules;

  protected ComponentContext context;


  @Activate
  public void activate(ComponentContext context) {
    this.context = context;
    this.indexRules = PropertiesUtil.toStringArray(context.getProperties().get(ElasticSearchIndexConfiguration.PROPERTY_INDEX_RULES));
//    if (PropertiesUtil.toBoolean(context.getProperties().get(ElasticSearchIndexConfiguration.PROPERTY_REINDEX), false)) {
      //TODO: start reindexing for this configuration
      //TODO: check if jobManager would be better
//      if (reindexService != null && scheduler != null) {
//        LOG.info("Scheduling reindexing for " + PropertiesUtil.toString(context.getProperties().get(ElasticSearchIndexConfiguration.PRIMARY_TYPE), StringUtils.EMPTY));
//        ScheduleOptions options = scheduler.NOW();
//        scheduler.schedule(reindexService, options);
//      }
//    }
  }

  /**
   *
   * @return
   */
  public String[] getIndexRules() {
    return indexRules;
  }


}
