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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dev.eth0.elasticsearch.aem.search.result.SearchResult.Hit;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Test for Serialization of a search result
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchResultTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private SearchResult getSearchResult(String path) {
    try {
      return mapper.readValue(this.getClass().getClassLoader().getResource(path), SearchResult.class);
    }
    catch (IOException ioe) {
      return null;
    }
  }

  @Test
  public void testSearchResultSerialization() {
    SearchResult result = getSearchResult("searchResult.json");
    assertNotNull(result);
    assertEquals(2, result.getTook());
    assertFalse(result.isTimed_out());
    assertNotNull(result.getShards());
    assertEquals(5, result.getShards().getTotal());
    assertEquals(4, result.getShards().getSuccessful());
    assertEquals(1, result.getShards().getFailed());

    assertEquals(2, result.getHits().getTotal());
    assertEquals(1.0, result.getHits().getMax_score(), 0.0);

    assertEquals(2, result.getHits().getHits().size());

    Hit hit = result.getHits().getHits().get(0);
    assertEquals("299fd3026fff551d5d38a6c3250cd356", hit.getId());
    assertEquals("page", hit.getType());
    assertEquals("idx", hit.getIndex());
    assertEquals(1.0, hit.getScore(), 0.0);
    assertEquals(4, hit.getSource().size());
  }

}
