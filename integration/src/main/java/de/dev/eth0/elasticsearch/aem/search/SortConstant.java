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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SortConstant {
  ORDER("order");

  @Getter(onMethod = @__(
          @JsonValue))
  private final String keyword;

  private SortConstant(String keyword) {
    this.keyword = keyword;
  }

  public enum SortOrderConstant {
    ASC("asc"),
    DESC("desc");
    @Getter(onMethod = @__(
            @JsonValue))
    private final String keyword;

    private SortOrderConstant(String keyword) {
      this.keyword = keyword;
    }

  }

  public enum SortModeConstant {
    MEDIAN("median"),
    MIN("min"),
    MAX("max"),
    SUM("sum"),
    AVG("avg");
    @Getter(onMethod = @__(
            @JsonValue))
    private final String keyword;

    private SortModeConstant(String keyword) {
      this.keyword = keyword;
    }

  }

}
