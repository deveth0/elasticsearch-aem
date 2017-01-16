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
package de.dev.eth0.elasticsearch.aem.contentbuilder;

import de.dev.eth0.elasticsearch.aem.indexing.IndexEntry;
import de.dev.eth0.elasticsearch.aem.indexing.contentbuilder.PageContentBuilder;
import de.dev.eth0.elasticsearch.aem.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PageContentBuilderTest {

  @Rule
  public AemContext context = AppAemContext.newAemContext();

  @Before
  public void setUp() {
    AppAemContext.loadSampleContent(context);
  }

  @Test
  public void testCreate() {
    PageContentBuilder builder = new PageContentBuilder() {
      @Override
      public String[] getIndexRules(String primaryType) {
        return new String[]{"foobar", "childFoo", "boolean", "multi", "cascade", "double", "ipsum", "cq:template"};
      }
    };
    IndexEntry entry = builder.create(AppAemContext.PAGE, context.resourceResolver());
    assertNotNull(entry);
    assertEquals(AppAemContext.PAGE, entry.getPath());
    assertEquals("page", entry.getType());
    assertEquals("/foo/template", entry.getContent().get("cq:template"));
    assertEquals("foobarValue", entry.getContent().get("foobar"));
    assertEquals("childValue", entry.getContent().get("childFoo"));

    assertTrue(entry.getContent("boolean", Boolean.class));

    String[] multi = entry.getContent("multi", String[].class);
    assertEquals(2, multi.length);
    assertEquals("foobar", multi[0]);
    assertEquals("ipsum", multi[1]);

    Object[] cascade = entry.getContent("cascade", Object[].class);
    assertEquals(3, cascade.length);
    assertEquals(String.class, cascade[0].getClass());
    assertEquals(String.class, cascade[1].getClass());
    assertEquals(String.class, cascade[2].getClass());

  }

}
