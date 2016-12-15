package se.atg.sam.ui.rest.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import se.atg.sam.helpers.JsonHelper;
import se.atg.sam.model.StringInDatabase;
import se.atg.sam.model.View;

public class StringInDatabaseSerializer extends StdSerializer<StringInDatabase> {

  private static final long serialVersionUID = 1L;

  protected StringInDatabaseSerializer() {
    super(StringInDatabase.class);
  }

  @Override
  public void serialize(StringInDatabase obj, JsonGenerator jgen, SerializerProvider sp) throws IOException {

    if (sp.getActiveView().isAssignableFrom(View.Db.class)) {
      jgen.writeString(obj.stringInDatabase());
    } else {
      final JsonSerializer<Object> beanSerializer = JsonHelper.getBeanSerializer(sp, obj.getClass());
      beanSerializer.serialize(obj, jgen, sp);
    }
  }
}
