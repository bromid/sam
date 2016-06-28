package se.atg.cmdb.ui.rest.serializer;

import java.io.IOException;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LinkSerializer extends StdSerializer<Link> {

  private static final long serialVersionUID = 1L;

  public LinkSerializer() {
    super(Link.class);
  }

  @Override
  public void serialize(Link link, JsonGenerator jgen, SerializerProvider sp) throws IOException, JsonProcessingException {
    jgen.writeStartObject();
    jgen.writeStringField("rel", link.getRel());
    jgen.writeStringField("href", link.getUri().toString());
    jgen.writeEndObject();
  }
}
