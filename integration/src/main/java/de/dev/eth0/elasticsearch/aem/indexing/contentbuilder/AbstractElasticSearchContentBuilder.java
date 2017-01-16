//$URL: $
//$Id: $
package de.dev.eth0.elasticsearch.aem.indexing.contentbuilder;

import de.dev.eth0.elasticsearch.aem.indexing.config.ElasticSearchIndexConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElasticSearchContentBuilder implements ElasticSearchContentBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractElasticSearchContentBuilder.class);

  private BundleContext context;

  @Activate
  public void activate(BundleContext context) {
    this.context = context;
  }

  /**
   * @param primaryType
   * @return List with all properties to index
   */
  protected String[] getIndexRules(String primaryType) {
    ElasticSearchIndexConfiguration config = getIndexConfig(primaryType);
    if (config != null) {
      return ArrayUtils.addAll(config.getIndexRules(), getFixedRules());
    }
    return getFixedRules();
  }

  /**
   *
   * @return Array with hardcoded rules
   */
  protected String[] getFixedRules() {
    return new String[0];
  }


  private ElasticSearchIndexConfiguration getIndexConfig(String primaryType) {
    try {
      ServiceReference[] serviceReferences = context.getServiceReferences(ElasticSearchIndexConfiguration.class.getName(),
              "(" + ElasticSearchIndexConfiguration.PROPERTY_BASE_PATH + "=" + primaryType + ")");
      if (serviceReferences != null && serviceReferences.length > 0) {
        return (ElasticSearchIndexConfiguration)context.getService(serviceReferences[0]);
      }
    }
    catch (InvalidSyntaxException | NullPointerException ex) {
      LOG.error("Exception during service lookup", ex);
    }
    LOG.info("Could not load a ElasticSearchConfiguration for primaryType " + primaryType);
    return null;
  }

  /**
   * Recursively searches all child-resources for the given resources and returns a map with all of them
   *
   * @param res
   * @param properties
   * @return
   */
  protected Map<String, Object> getProperties(Resource res, String[] properties) {
    //TODO: add support for * property
    ValueMap vm = res.getValueMap();
    Map<String, Object> ret = Arrays.stream(properties)
            .filter(property -> vm.containsKey(property))
            .collect(Collectors.toMap(Function.identity(), property -> vm.get(property)));

    for (Resource child : res.getChildren()) {
      Map<String, Object> props = getProperties(child, properties);
      // merge properties
      props.entrySet().forEach(entry -> {
        //TODO: Merge properties
        if (!ret.containsKey(entry.getKey())) {
          ret.put(entry.getKey(), entry.getValue());
        }
        else {
          ret.put(entry.getKey(), mergeProperties(ret.get(entry.getKey()), entry.getValue()));
        }
      });
    }
    return ret;
  }

  private Object[] mergeProperties(Object obj1, Object obj2) {
    List<Object> tmp = new ArrayList<>();
    addProperty(tmp, obj1);
    addProperty(tmp, obj2);
    return tmp.toArray(new Object[tmp.size()]);
  }

  private void addProperty(List<Object> list, Object property) {
    if (property.getClass().isArray()) {
      list.addAll(Arrays.asList((Object[])property));
    }
    else {
      list.add(property);
    }
  }
}
