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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dev.eth0.elasticsearch.aem.search.result.SearchResult;
import de.dev.eth0.elasticsearch.aem.service.ElasticSearchService;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search Search Builder
 */
public class Search {

  private static final Logger LOG = LoggerFactory.getLogger(Search.class);
  private final ElasticSearchService elasticSearchService;

  private final String index;
  @Getter(onMethod = @__(
          @JsonAnyGetter))
  private final Map<FilterConstant, Object> filter = new LinkedHashMap<>();


  public Search(ElasticSearchService elasticSearchService, String index) {
    this.elasticSearchService = elasticSearchService;
    this.index = index;
  }

  public SearchResult perform() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String queryString = serialize();
      LOG.info("Query: " + queryString);
      Response response = elasticSearchService.getRestClient().performRequest(
              "GET",
              "/" + index + "/_search",
              Collections.<String, String>emptyMap(),
              new StringEntity(queryString, "UTF-8"));

      HttpEntity entity = response.getEntity();
      return mapper.readValue(entity.getContent(), SearchResult.class);
    }
    catch (IOException ex) {
      LOG.error("Could not perform query", ex);
    }
    return null;
  }

  public RootQuery query() {
    if (!filter.containsKey(FilterConstant.QUERY)) {
      filter.put(FilterConstant.QUERY, new RootQuery());
    }
    return (RootQuery)filter.get(FilterConstant.QUERY);
  }

  public Search size(int size) {
    filter.put(FilterConstant.SIZE, size);
    return this;
  }

  public Search from(int from) {
    filter.put(FilterConstant.FROM, from);
    return this;
  }

  public Search minScore(double minScore) {
    filter.put(FilterConstant.MIN_SCORE, minScore);
    return this;
  }

  public Sort sort() {
    if (!filter.containsKey(FilterConstant.SORT)) {
      filter.put(FilterConstant.SORT, new Sort());
    }
    return (Sort)filter.get(FilterConstant.SORT);
  }

  public Highlight highlight() {
    if (!filter.containsKey(FilterConstant.HIGHLIGHT)) {
      filter.put(FilterConstant.HIGHLIGHT, new Highlight());
    }
    return (Highlight)filter.get(FilterConstant.HIGHLIGHT);
  }

  /**
   * Serialize this Search to a JSON
   *
   * @return
   */
  public String serialize() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    }
    catch (JsonProcessingException ex) {
      LOG.error("Could not serialize search", ex);
    }
    return StringUtils.EMPTY;
  }


}
