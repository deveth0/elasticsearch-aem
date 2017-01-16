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
import com.fasterxml.jackson.annotation.JsonValue;
import de.dev.eth0.elasticsearch.aem.search.SortConstant.SortModeConstant;
import de.dev.eth0.elasticsearch.aem.search.SortConstant.SortOrderConstant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * The sort options for a Search
 */
public class Sort {

  @Getter(onMethod = @__(
          @JsonValue))
  protected final List<Map<String, SortField>> sort = new LinkedList<>();

  public SortField byField(String fieldName) {
    SortField ret = new SortField();
    Map<String, SortField> map = new HashMap<>();
    map.put(fieldName, ret);
    sort.add(map);
    return ret;
  }

  /**
   * A single field used for sorting
   */
  public static class SortField {

    @Getter(onMethod = @__(
            @JsonAnyGetter))
    private final Map<SortConstant, Object> sort = new LinkedHashMap<>();

    /**
     * Sort Order
     *
     * @param order
     * @return
     */
    public SortField order(SortOrderConstant order) {
      sort.put(SortConstant.ORDER, order);
      return this;
    }

    /**
     * Sort Mode
     *
     * @param mode
     * @return
     */
    public SortField mode(SortModeConstant mode) {
      sort.put(SortConstant.ORDER, mode);
      return this;
    }
  }
}
