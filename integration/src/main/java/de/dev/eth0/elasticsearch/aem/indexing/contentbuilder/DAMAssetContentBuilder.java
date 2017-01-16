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
package de.dev.eth0.elasticsearch.aem.indexing.contentbuilder;

import com.day.cq.dam.api.Asset;
import de.dev.eth0.elasticsearch.aem.indexing.IndexEntry;
import de.dev.eth0.elasticsearch.aem.indexing.config.ElasticSearchIndexConfiguration;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content Builder for DAM Assets
 */
@Component(immediate = true)
@Service(ElasticSearchContentBuilder.class)
@Property(name = ElasticSearchIndexConfiguration.PRIMARY_TYPE, value = DAMAssetContentBuilder.PRIMARY_TYPE_VALUE)
public class DAMAssetContentBuilder extends AbstractElasticSearchContentBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(DAMAssetContentBuilder.class);
  public static final String PRIMARY_TYPE_VALUE = "dam:Asset";

  private static final String[] FIXED_RULES = {"dc:title", "dc:description", "jcr:lastModified"};

  @Override
  public IndexEntry create(String path, @Nonnull ResourceResolver resolver) {
    String[] indexRules = getIndexRules(PRIMARY_TYPE_VALUE);
    if (ArrayUtils.isNotEmpty(indexRules)) {
    Resource res = resolver.getResource(path);
    if (res != null) {
      Asset asset = res.adaptTo(Asset.class);
      if (asset != null) {
        IndexEntry ret = new IndexEntry("idx", "asset", path);
        ret.addContent(getProperties(res, indexRules));
        return ret;
      }
      LOG.error("Could not adapt asset");
      }
    }
    return null;
  }

  @Override
  protected String[] getFixedRules() {
    return FIXED_RULES;
  }
}
