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
package de.dev.eth0.elasticsearch.aem.search.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Search Result from ElasticSearch
 */
public class SearchResult {

  @Getter
  @Setter
  private int took;

  @Getter
  @Setter
  private boolean timed_out;

  @JsonProperty(value = "_shards")
  @Getter
  @Setter
  private Shards shards;

  @Getter
  @Setter
  private Hits hits;

  public static class Shards {

    @Getter
    @Setter
    private int total;

    @Getter
    @Setter
    private int successful;

    @Getter
    @Setter
    private int failed;
  }

  public static class Hits {

    @Getter
    @Setter
    private int total;

    @Getter
    @Setter
    private double max_score;

    @Getter
    @Setter
    private List<Hit> hits = new ArrayList<>();

  }

  public static class Hit {

    @JsonProperty(value = "_index")
    @Getter
    @Setter
    private String index;

    @JsonProperty(value = "_type")
    @Getter
    @Setter
    private String type;

    @JsonProperty(value = "_id")
    @Getter
    @Setter
    private String id;

    @JsonProperty(value = "_score")
    @Getter
    @Setter
    private Double score;

    @JsonProperty(value = "_source")
    @Getter
    @Setter
    private Map<String, Object> source;

    @JsonProperty(value = "sort")
    @Getter
    @Setter
    private List<Object> sort;

    @JsonProperty(value = "highlight")
    @Getter
    @Setter
    private Map<String, List<Object>> highlight;
  }
}
