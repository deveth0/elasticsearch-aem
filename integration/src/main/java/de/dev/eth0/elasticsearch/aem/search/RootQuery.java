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

import java.util.HashMap;
import java.util.Map;

/**
 * The root query which can only contain one child
 */
public class RootQuery extends Query {

  protected final Map<QueryConstant, Object> content = new HashMap<>();

  @Override
  public BoolQuery bool() {
    if (content.containsKey(QueryConstant.BOOL)) {
      return (BoolQuery)content.get(QueryConstant.BOOL);
    }
    return super.bool();
  }

  @Override
  protected void add(QueryConstant queryType, Object obj) {
    content.clear();
    content.put(queryType, obj);
  }

  @Override
  public Object getQuery() {
    return content;
  }

}
