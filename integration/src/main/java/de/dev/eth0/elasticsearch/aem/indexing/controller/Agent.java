/*
 * Copyright 2017 dev-eth0.
 *
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
 */
package de.dev.eth0.elasticsearch.aem.indexing.controller;

import de.dev.eth0.elasticsearch.aem.service.ElasticSearchService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = {
  SlingHttpServletRequest.class, Resource.class}
)
public class Agent {

  @SlingObject
  protected Resource resource;

  @Getter
  private boolean valid;

  @ValueMapValue
  @Default(values = "false")
  private String enabled;

  @Inject
  @Optional
  private ElasticSearchService elasticSearchService;

  @PostConstruct
  protected void activate() {
    valid = StringUtils.equals(enabled, "true") && elasticSearchService != null && elasticSearchService.getRestClient() != null;
  }

}
