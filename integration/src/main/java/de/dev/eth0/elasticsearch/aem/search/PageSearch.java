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
package de.dev.eth0.elasticsearch.aem.search;

import de.dev.eth0.elasticsearch.aem.service.ElasticSearchService;

/**
 * Extended version of a search with page-specific properties/methods.
 */
public class PageSearch extends Search {

  public PageSearch(ElasticSearchService elasticSearchService, String index) {
    super(elasticSearchService, index);
  }

  /**
   *
   * @param resourceTypes of the templates to consider
   * @return
   */
  public PageSearch templates(String... resourceTypes) {
    query().bool().filter().terms("cq:template", resourceTypes);
    return this;
  }

  /**
   * @param path root Path for this search
   * @return
   */
  public PageSearch path(String path) {
    query().bool().filter().wildcard("path", path + "*");
    return this;
  }

  /**
   *
   * @param title
   * @return
   */
  public PageSearch title(String title) {
    query().bool().must().match("jcr:title", title);
    return this;
  }

  /**
   *
   * @param newestFirst
   * @return
   */
  public PageSearch newestFirst(boolean newestFirst) {
    sort().byField("cq:lastModified").order(newestFirst ? SortConstant.SortOrderConstant.DESC : SortConstant.SortOrderConstant.ASC);
    return this;
  }

}
