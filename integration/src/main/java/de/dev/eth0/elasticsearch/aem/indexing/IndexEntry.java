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
package de.dev.eth0.elasticsearch.aem.indexing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data object holding all information required to index/deindex an entity
 */
public class IndexEntry {
  private static final Logger LOG = LoggerFactory.getLogger(IndexEntry.class);
  /**
   * Index for the entity, used as elasticsearch index
   */
  @Getter
  @Setter
  private String index;
  /**
   * Type of the entity (e.g. page or damasset), used as elasticsearch type
   */
  @Getter
  @Setter
  private String type;
  /*
   * ID of the entity, used as elasticsearch ID
   */
  @Getter
  @Setter
  private String id;
  /**
   * Content to index
   */
  @Setter
  private Map<String, Object> content = new HashMap<>();

  public IndexEntry() {
  }

  /**
   *
   * @param index
   * @param type
   * @param path
   */
  public IndexEntry(String index, String type, String path) {
    this.index = index;
    this.type = type;
    setPath(path);
  }

  public void addContent(String key, Object value) {
    content.put(key, value);
  }

  public void setPath(String path) {
    this.content.put("path", path);
    this.id = DigestUtils.md5Hex(path);
  }

  public String getPath() {
    return getContent("path", String.class);
  }

  public void addContent(Map<String, Object> properties) {
    content.putAll(properties);
  }

  /**
   *
   * @return unmodifiable map with the content
   */
  public Map<String, Object> getContent() {
    return Collections.unmodifiableMap(content);
  }

  /**
   * Returns the content for the given key or null if cast fails
   *
   * @param <T>
   * @param key
   * @param type
   * @return
   */
  public <T> T getContent(final String key, final Class<T> type) {
    try {
      return (T)content.get(key);
    }
    catch (ClassCastException cce) {
      LOG.warn("Could not cast " + key + " to " + type, cce);
    }
    return null;
  }


}
