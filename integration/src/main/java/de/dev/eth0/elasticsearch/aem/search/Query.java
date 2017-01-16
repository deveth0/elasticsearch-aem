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

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * A ElasticSearch Query
 */
public class Query {

  protected final List<Map<QueryConstant, Object>> query = new LinkedList<>();

  @JsonValue
  protected Object getQuery() {
    return query;
  }

  public BoolQuery bool() {
    BoolQuery ret = new BoolQuery();
    add(QueryConstant.BOOL, ret);
    return ret;
  }
  /**
   * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/5.1/query-dsl-wildcard-query.html">
   * https://www.elastic.co/guide/en/elasticsearch/reference/5.1/query-dsl-wildcard-query.html</a>
   * @param field
   * @param value
   * @return
   */
  public Query wildcard(String field, String value) {
    put(QueryConstant.WILDCARD, field, value);
    return this;
  }

  /**
   * Match Query for text, numerics and dates
   *
   * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html">
   *
   * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html</a>
   * @param field
   * @param value
   * @return
   */
  public Query match(String field, String value) {
    put(QueryConstant.MATCH, field, value);
    return this;
  }

  /**
   * Multi Match Query which allows multi-field queries.
   *
   * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html">
   *
   * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html</a>
   * @param fields
   * @param value
   * @return
   */
  public Query multi_match(String[] fields, String value) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(QueryConstant.FIELDS.getKeyword(), fields);
    map.put(QueryConstant.QUERY.getKeyword(), value);
    add(QueryConstant.MULTI_MATCH, map);
    return this;
  }

  public Query term(String field, String value) {
    put(QueryConstant.TERM, field, value);
    return this;
  }

  public Query terms(String field, String... value) {
    put(QueryConstant.TERMS, field, value);
    return this;
  }

  protected void put(QueryConstant queryType, String field, String value) {
    Map<String, String> map = new HashMap<>();
    map.put(field, value);
    add(queryType, map);
  }

  protected void put(QueryConstant queryType, String field, String... value) {
    Map<String, String[]> map = new HashMap<>();
    map.put(field, value);
    add(queryType, map);
  }

  protected void add(QueryConstant queryType, Object obj) {
    Map<QueryConstant, Object> add = new HashMap<>();
    add.put(queryType, obj);
    query.add(add);
  }

}
