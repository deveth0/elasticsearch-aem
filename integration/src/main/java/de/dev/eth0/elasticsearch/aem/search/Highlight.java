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
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Highlight options for a search
 */
public class Highlight {

  @Getter(onMethod = @__(
          @JsonValue))
  protected final Map<HighlightConstant, Object> highlight = new LinkedHashMap<>();

  public Highlight tags(String pretag, String posttag) {
    return tags(new String[]{pretag}, new String[]{posttag});
  }

  public Highlight tags(String[] pretags, String[] posttags) {
    highlight.put(HighlightConstant.PRE_TAGS, pretags);
    highlight.put(HighlightConstant.POST_TAGS, posttags);
    return this;
  }

  public HighlightField field(String fieldName) {
    if (!highlight.containsKey(HighlightConstant.FIELDS)) {
      highlight.put(HighlightConstant.FIELDS, new LinkedHashMap<>());
    }
    HighlightField ret = new HighlightField();
    ((Map)highlight.get(HighlightConstant.FIELDS)).put(fieldName, ret);
    return ret;
  }

  /**
   * A single field used for highlighting
   */
  public static class HighlightField {
    @Getter(onMethod = @__(
            @JsonAnyGetter))
    private final Map<String, Object> properties = new LinkedHashMap<>();
  }
}
