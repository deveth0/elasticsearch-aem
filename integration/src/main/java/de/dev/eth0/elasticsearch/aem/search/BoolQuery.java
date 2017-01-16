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
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

public class BoolQuery {

  @Getter(onMethod = @__(
          @JsonAnyGetter))
  protected final Map<QueryConstant, Query> query = new LinkedHashMap<>();

  public Query should() {
    return add(QueryConstant.SHOULD);
  }

  public Query must() {
    return add(QueryConstant.MUST);
  }

  public Query must_not() {
    return add(QueryConstant.MUST_NOT);
  }

  public Query filter() {
    return add(QueryConstant.FILTER);
  }

  private Query add(QueryConstant queryType) {
    if (!query.containsKey(queryType)) {
      query.put(queryType, new Query());
    }
    return query.get(queryType);
  }

}
