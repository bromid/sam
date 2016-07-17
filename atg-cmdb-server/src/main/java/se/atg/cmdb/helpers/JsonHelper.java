package se.atg.cmdb.helpers;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import se.atg.cmdb.model.Group;
import se.atg.cmdb.model.View;
import se.atg.cmdb.ui.rest.serializer.LinkDeserializer;
import se.atg.cmdb.ui.rest.serializer.LinkSerializer;

public abstract class JsonHelper {

  private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);

  public static UpdateMetaResult updateMetaForUpdate(Document bson, Optional<String> hash, String updatedBy) {

    final Document updatedMeta = (Document) bson.remove("meta");

    final Date now = Date.from(Instant.now());
    updatedMeta.put("refreshed", now);
    updatedMeta.put("refreshedBy", updatedBy);

    final String newHash = DigestUtils.sha1Hex(bson.toJson());
    if (hash.isPresent() && hash.get().equals(newHash)) {
      bson.put("meta", updatedMeta);
      return new UpdateMetaResult() {{
        updated = false;
        meta = updatedMeta;
      }};
    } else {
      updatedMeta.put("hash", newHash);
      updatedMeta.put("updated", now);
      updatedMeta.put("updatedBy", updatedBy);
      bson.put("meta", updatedMeta);
      return new UpdateMetaResult() {{
        updated = true;
        meta = updatedMeta;
      }};
    }
  }

  public static Document addMetaForCreate(Object node, String createdBy, ObjectMapper objectMapper) {
    final String json = JsonHelper.objectToJson(node, objectMapper, View.Db.class);
    return addMetaForCreate(json, createdBy);
  }

  public static Document addMetaForCreate(String json, String createdBy) {

    final String hash = DigestUtils.sha1Hex(json);
    final Document bson = Document.parse(json);

    final Date now = Date.from(Instant.now());
    bson.put("meta", new Document()
      .append("hash", hash)
      .append("updated", now)
      .append("updatedBy", createdBy)
      .append("created", now)
      .append("createdBy", createdBy)
      .append("refreshed", now)
      .append("refreshedBy", createdBy));
    return bson;
  }

  public static void merge(Document base, JsonNode update, ObjectMapper objectMapper, String... ignoredFields) {

    update.fields().forEachRemaining(t -> {

      final String key = t.getKey();
      final JsonNode node = t.getValue();
      if (ArrayUtils.contains(ignoredFields, key)) {
        logger.trace("Ignored field: {}", key);
      } else if (node.isObject()) {

        final Document existing = base.get(key, Document.class);
        if (existing != null) {
          logger.trace("Merge object {}", key);
          merge(existing, node, objectMapper);
        } else {
          logger.trace("Add new object {}", key);
          base.put(key, jsonToBson(node, objectMapper));
        }
      } else if (node.isArray()) {
        logger.trace("Add or update array {}", key);
        base.put(key, jsonToObject(node, objectMapper));
      } else {
        logger.trace("Add or update object {}", key);
        base.put(key, jsonToObject(node, objectMapper));
      }
    });
  }

  public static Object jsonToObject(JsonNode node, ObjectMapper objectMapper) {
    try {
      return objectMapper.treeToValue(node, Object.class);
    } catch (JsonProcessingException exc) {
      logger.error("Failed to generate object for: " + node, exc);
      return null;
    }
  }

  public static String objectToJson(Object node, ObjectMapper objectMapper, Class<?> view) {
    try {
      return objectMapper.writerWithView(view).writeValueAsString(node);
    } catch (JsonProcessingException exc) {
      logger.error("Failed to generate object for: " + node, exc);
      return null;
    }
  }

  public static JsonNode objectToJsonNode(Object node, ObjectMapper objectMapper) {
    try {
      final TokenBuffer buf = new TokenBuffer(objectMapper, false);
      objectMapper.writerWithView(View.Db.class).writeValue(buf, node);

      final JsonParser jp = buf.asParser();
      return objectMapper.readTree(jp);
    } catch (IOException exc) {
      logger.error("Failed to generate object for: " + node, exc);
      return null;
    }
  }

  public static Document jsonToBson(JsonNode node, ObjectMapper objectMapper) {
    try {
      final String json = objectMapper.writerWithView(View.Db.class).writeValueAsString(node);
      return Document.parse(json);
    } catch (JsonProcessingException exc) {
      logger.error("Failed to generate json for: " + node, exc);
      return null;
    }
  }

  public static Document entityToBson(Object entity, ObjectMapper objectMapper) {
    try {
      final String json = objectMapper.writerWithView(View.Db.class).writeValueAsString(entity);
      return Document.parse(json);
    } catch (JsonProcessingException exc) {
      logger.error("Failed to generate json for: " + entity, exc);
      return null;
    }
  }

  public static List<? extends Document> entitiesToBson(List<Group> groupsToAdd, ObjectMapper objectMapper) {
    return groupsToAdd.stream()
      .map(t -> entityToBson(t, objectMapper))
      .collect(Collectors.toList());
  }

  public static JsonSerializer<Object> getBeanSerializer(SerializerProvider sp, Class<?> cls) throws JsonMappingException {

    final SerializationConfig config = sp.getConfig();
    final JavaType type = config.constructType(cls);
    final BeanDescription beanDesc = config.introspect(type);
    return BeanSerializerFactory.instance.findBeanSerializer(sp, type, beanDesc);
  }

  public static ObjectMapper configureObjectMapper(ObjectMapper objectMapper, Class<?> view) {
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.setConfig(objectMapper.getSerializationConfig().withView(view));
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new SimpleModule() {
      private static final long serialVersionUID = 1L;
      {
        addSerializer(new LinkSerializer());
        addDeserializer(Link.class, new LinkDeserializer());
      }
    });
    return objectMapper;
  }

  public static class UpdateMetaResult {
    public boolean updated;
    public Document meta;
  }
}
