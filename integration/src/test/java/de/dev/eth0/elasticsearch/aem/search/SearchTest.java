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

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchTest {

  private Search underTest;

  @Before
  public void setUp() {
    underTest = new Search(null, "idx");
  }

  @Test
  public void testEmptySearch() {
    assertEquals("{}", underTest.serialize());
  }

  @Test
  public void testSearchFilter() {
    underTest.from(10).size(20).minScore(0.5);
    assertEquals("{\"from\":10,\"size\":20,\"min_score\":0.5}", underTest.serialize());
  }

  @Test
  public void testSimpleSearch() throws IOException {
    underTest.query().bool().must().term("foo", "bar");
    assertEquals("{\"query\":"
            + "{\"bool\":"
            + "{\"must\":"
            + "["
            + "{\"term\":"
            + "{\"foo\":\"bar\"}"
            + "}]}}}", underTest.serialize());
  }

  @Test
  public void testMultipleCriteriaSearch() throws IOException {
    Query must = underTest.query().bool().must();
    must.terms("cq:template", "/apps/foo", "/apps/bar");
    must.match("foo", "bar");
    assertEquals("{\"query\":"
            + "{\"bool\":"
            + "{\"must\":"
            + "["
            + "{\"terms\":"
            + "{\"cq:template\":"
            + "["
            + "\"/apps/foo\","
            + "\"/apps/bar\""
            + "]"
            + "}"
            + "},"
            + "{"
            + "\"match\":{"
            + "\"foo\":\"bar\""
            + "}}]}}}", underTest.serialize());
  }

  @Test
  public void testMultipleCriteriaFilterSearch() throws IOException {
    BoolQuery bool = underTest.query().bool();
    Query must = bool.must();
    must.match("foo", "bar");
    bool.filter().terms("cq:template", "/apps/foo", "/apps/bar");
    assertEquals("{\"query\":"
            + "{\"bool\":"
            + "{\"must\":"
            + "["
            + "{"
            + "\"match\":{"
            + "\"foo\":\"bar\""
            + "}}],"
            + "\"filter\":"
            + "["
            + "{\"terms\":"
            + "{\"cq:template\":"
            + "["
            + "\"/apps/foo\","
            + "\"/apps/bar\""
            + "]"
            + "}}]}}}", underTest.serialize());
  }

  @Test
  public void testSortSearch() throws IOException {
    Sort sort = underTest.sort();
    sort.byField("foo").order(SortConstant.SortOrderConstant.DESC);
    sort.byField("bar").order(SortConstant.SortOrderConstant.ASC);
    sort.byField("_score");
    assertEquals("{\"sort\":["
            + "{\"foo\":{\"order\":\"desc\"}},"
            + "{\"bar\":{\"order\":\"asc\"}},"
            + "{\"_score\":{}}"
            + "]}", underTest.serialize());
  }

  @Test
  public void testHighlightSearch() throws IOException {
    Highlight highlight = underTest.highlight();
    highlight.tags("[foo]", "[/foo]");
    highlight.field("jcr:title");
    underTest.query().bool().must().term("foo", "bar");
    assertEquals("{\"highlight\":"
            + "{\"pre_tags\":["
            + "\"[foo]\"],"
            + "\"post_tags\":["
            + "\"[/foo]\"],\"fields\":{\"jcr:title\":{}}},"
            + "\"query\":"
            + "{\"bool\":"
            + "{\"must\":"
            + "[{\"term\":"
            + "{\"foo\":\"bar\"}}]}}}", underTest.serialize());
  }

}
