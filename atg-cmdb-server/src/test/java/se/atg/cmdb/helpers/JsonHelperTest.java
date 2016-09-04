package se.atg.cmdb.helpers;

import java.util.Arrays;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import se.atg.cmdb.model.Server;
import se.atg.cmdb.model.View;

public class JsonHelperTest {

  @Test
  public void deepMerge() {

    final String server_profile_update = "v3";
    final String atg_common = "v1";
    final String attributes_files_puppet = "/etc/sam-collector/attributes/puppet";
    final String vmware_collector_blade = "blade2";

    final ObjectMapper mapper = JsonHelper.configureObjectMapper(new ObjectMapper(), View.Api.class);

    final Server update = new Server() {{
      hostname = "newHostname";
      os = new Os() {{
        name = "Windows XP";
      }};
      attributes = ImmutableMap.of(
        "server_collector", ImmutableMap.of("puppet", ImmutableMap.of("server_profile", server_profile_update))
      );
    }};
    final JsonNode updateNode = JsonHelper.objectToJsonNode(update, mapper);

    final Server existing = new Server() {{
      hostname = "oldHostname";
      environment = "oldEnvironment";
      os = new Os() {{
        name = "Windows 2000";
        type = "windows";
      }};
      attributes = ImmutableMap.of(
        "server_collector", ImmutableMap.of(
          "puppet", ImmutableMap.of("atg_common", atg_common, "server_profile", "v2"),
          "attributes_files", ImmutableMap.of("puppet", attributes_files_puppet)
        ),
        "vmware_collector", ImmutableMap.of("blade", vmware_collector_blade)
      );
    }};
    final Document existingDocument = JsonHelper.entityToBson(existing, mapper);

    JsonHelper.merge(existingDocument, updateNode, mapper);

    Assert.assertEquals(update.hostname, existingDocument.get("hostname"));
    Assert.assertEquals(existing.environment, existingDocument.get("environment"));
    Assert.assertEquals(update.os.name, existingDocument.get("os", Document.class).get("name"));
    Assert.assertEquals(existing.os.type, existingDocument.get("os", Document.class).get("type"));
    Assert.assertEquals(server_profile_update, getField(existingDocument, "attributes", "server_collector", "puppet", "server_profile"));
    Assert.assertEquals(atg_common, getField(existingDocument, "attributes", "server_collector", "puppet", "atg_common"));
    Assert.assertEquals(attributes_files_puppet, getField(existingDocument, "attributes", "server_collector", "attributes_files", "puppet"));
    Assert.assertEquals(vmware_collector_blade, getField(existingDocument, "attributes", "vmware_collector", "blade"));
  }

  @Test
  public void shallowMerge() {

    final int mergeDepth = 1;
    final String server_profile_update = "v3";
    final String atg_common = "v1";
    final String attributes_files_puppet = "/etc/sam-collector/attributes/puppet";
    final String vmware_collector_blade = "blade2";

    final ObjectMapper mapper = JsonHelper.configureObjectMapper(new ObjectMapper(), View.Api.class);

    final Server update = new Server() {{
      hostname = "newHostname";
      os = new Os() {{
        name = "Windows XP";
      }};
      attributes = ImmutableMap.of(
        "server_collector", ImmutableMap.of("puppet", ImmutableMap.of("server_profile", server_profile_update))
      );
    }};
    final JsonNode updateNode = JsonHelper.objectToJsonNode(update, mapper);

    final Server existing = new Server() {{
      hostname = "oldHostname";
      environment = "oldEnvironment";
      os = new Os() {{
        name = "Windows 2000";
        type = "windows";
        attributes = ImmutableMap.of("server_collector","existing_attributes");
      }};
      attributes = ImmutableMap.of(
        "server_collector", ImmutableMap.of(
          "puppet", ImmutableMap.of("atg_common", atg_common, "server_profile", "v2"),
          "attributes_files", ImmutableMap.of("puppet", attributes_files_puppet)
        ),
        "vmware_collector", ImmutableMap.of("blade", vmware_collector_blade)
      );
    }};
    final Document existingDocument = JsonHelper.entityToBson(existing, mapper);

    JsonHelper.merge(existingDocument, updateNode, mergeDepth, mapper);

    Assert.assertEquals(update.hostname, existingDocument.get("hostname"));
    Assert.assertEquals(existing.environment, existingDocument.get("environment"));
    Assert.assertEquals(update.os.name, existingDocument.get("os", Document.class).get("name"));
    Assert.assertEquals(existing.os.type, existingDocument.get("os", Document.class).get("type"));
    Assert.assertEquals(vmware_collector_blade, getField(existingDocument, "attributes", "vmware_collector", "blade"));
    Assert.assertEquals(server_profile_update, getField(existingDocument, "attributes", "server_collector", "puppet", "server_profile"));
    Assert.assertNull(getField(existingDocument, "attributes", "server_collector", "puppet", "atg_common"));
    Assert.assertNull(getField(existingDocument, "attributes", "server_collector", "attributes_files"));
  }

  @Test
  public void entityToBsonWithJsonViews() {

    final WithExcludes expected = new WithExcludes();

    final ObjectMapper mapper = JsonHelper.configureObjectMapper(new ObjectMapper(), View.Api.class);
    final Document bson = JsonHelper.entityToBson(expected, mapper);

    Assert.assertNull(bson.getString("api"));
    Assert.assertEquals(expected.db, bson.getString("db"));
    Assert.assertEquals(expected.defaultView, bson.getString("defaultView"));
  }

  private static Object getField(Document document, String... path) {

    Document curr = document;
    for (String field : Arrays.copyOfRange(path, 0, path.length - 1)) {
      curr = curr.get(field, Document.class);
    }
    return curr.get(path[path.length - 1]);
  }

  static class WithExcludes {

    @JsonView(View.Api.class)
    public final String api = "API";
    @JsonView(View.Db.class)
    public final String db = "DB";
    public final String defaultView = "NO-VIEW";
  }
}
