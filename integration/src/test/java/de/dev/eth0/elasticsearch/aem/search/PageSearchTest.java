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
public class PageSearchTest {

  private PageSearch underTest;

  @Before
  public void setUp() {
    underTest = new PageSearch(null, "idx");
  }

  @Test
  public void testEmptySearch() {
    assertEquals("{}", underTest.serialize());
  }

  @Test
  public void testTemplateSearch() throws IOException {
    underTest.templates("/apps/foo", "/apps/bar");
    assertEquals("{\"query\":"
            + "{\"bool\":"
            + "{\"filter\":"
            + "["
            + "{\"terms\":"
            + "{\"cq:template\":"
            + "["
            + "\"/apps/foo\","
            + "\"/apps/bar\""
            + "]"
            + "}"
            + "}]}}}", underTest.serialize());
  }

  @Test
  public void testTitleTemplateSearch() throws IOException {
    underTest.title("foobar");
    underTest.templates("/apps/foo", "/apps/bar");
    assertEquals("{\"query\":"
            + "{\"bool\":"
            + "{\"must\":[{"
            + "\"match\":{"
            + "\"jcr:title\":\"foobar\""
            + "}}],"
            + "\"filter\":"
            + "["
            + "{\"terms\":"
            + "{\"cq:template\":"
            + "["
            + "\"/apps/foo\","
            + "\"/apps/bar\""
            + "]"
            + "}"
            + "}]}}}", underTest.serialize());
  }

}
