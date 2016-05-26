package se.atg.cmdb.helpers;

import java.time.Instant;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JSONHelper {
	
	static final Logger logger = LoggerFactory.getLogger(JSONHelper.class);

	public static boolean updateMetaForUpdate(Document bson, String updatedBy) {

		final Document meta = (Document) bson.remove("meta");

		final Date now = Date.from(Instant.now());
		meta.put("refreshed", now);
		meta.put("refreshedBy", updatedBy);

		final String existingHash = Mapper.getHash(meta);
		final String newHash = DigestUtils.sha1Hex(bson.toJson());
		if (newHash.equals(existingHash)) {
			bson.put("meta", meta);
			return false;
		} else {
			meta.put("hash", newHash);
			meta.put("updated", now);
			meta.put("updatedBy", updatedBy);
			bson.put("meta", meta);
			return true;
		}
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
			.append("refreshedBy", createdBy)
		);
		return bson;
	}

	public static void merge(Document base, JsonNode update, ObjectMapper objectMapper, String... ignoredFields) {

		update.fields().forEachRemaining(t->{

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

	private static Object jsonToObject(JsonNode node, ObjectMapper objectMapper) {
		try {
			return objectMapper.treeToValue(node, Object.class);
		} catch (JsonProcessingException exc) {
			logger.error("Failed to generate object for: " + node, exc);
			return null;
		}
	}

	public static Document jsonToBson(JsonNode node, ObjectMapper objectMapper) {
		try {
			final String json = objectMapper.writeValueAsString(node);
			return Document.parse(json);
		} catch (JsonProcessingException exc) {
			logger.error("Failed to generate json for: " + node, exc);
			return null;
		}
	}
}