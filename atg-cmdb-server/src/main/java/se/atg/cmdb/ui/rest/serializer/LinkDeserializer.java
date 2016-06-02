package se.atg.cmdb.ui.rest.serializer;

import java.io.IOException;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class LinkDeserializer extends JsonDeserializer<Link> {

	@Override
	public Class<Link> handledType() {
		return Link.class;
	}

	@Override
	public Link deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		final ObjectCodec oc = jsonParser.getCodec();
        final JsonNode node = oc.readTree(jsonParser);

        final JsonNode href = node.get("href");
        if (href == null) {
        	return null;
        }

        final Builder linkBuilder = Link.fromUri(href.asText());
        final JsonNode rel = node.get("rel");
        if (rel != null) {
        	linkBuilder.rel(rel.asText());
        }
        return linkBuilder.build();
	}
}
