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
package de.dev.eth0.elasticsearch.aem.testcontext;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;
import java.io.IOException;
import org.apache.sling.api.resource.PersistenceException;

import static org.junit.Assert.assertNotNull;

public final class AppAemContext {

  public static final String CONTENT_ROOT = "/content";
  public static final String PAGE = CONTENT_ROOT + "/foobar";


  private AppAemContext() {
    // static methods only
  }

  public static AemContext newAemContext() {
    return new AemContext(new SetUpCallback(null));
  }

  public static AemContext newAemContext(AemContextCallback callback) {
    return new AemContext(new SetUpCallback(callback));
  }


  public static void loadSampleContent(AemContext context) {
    context.load().json("/content.json", AppAemContext.CONTENT_ROOT);
    Page currentPage = context.pageManager().getPage(AppAemContext.PAGE);
    assertNotNull(currentPage);
    context.currentPage(currentPage);
  }

  /**
   * Custom set up rules required in all unit tests.
   */
  private static final class SetUpCallback implements AemContextCallback {

    private final AemContextCallback testCallback;

    public SetUpCallback(AemContextCallback testCallback) {
      this.testCallback = testCallback;
    }

    @Override
    public void execute(AemContext context) throws PersistenceException, IOException {

      // call test-specific callback first
      if (testCallback != null) {
        testCallback.execute(context);
      }

    }
  }

}
