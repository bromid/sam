package se.atg.cmdb.ui.rest.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Tag;
import se.atg.cmdb.model.View;

public class TagSerializer extends StdSerializer<Tag>{

  private static final long serialVersionUID = 1L;

  public TagSerializer() {
    super(Tag.class);
  }

  @Override
  public void serialize(Tag tag, JsonGenerator jgen, SerializerProvider sp) throws IOException {

    if (sp.getActiveView().isAssignableFrom(View.Db.class)) {
      jgen.writeString(tag.name);
    } else {
      JsonHelper.getBeanSerializer(sp, _handledType).serialize(tag, jgen, sp);
    }
  }
}
