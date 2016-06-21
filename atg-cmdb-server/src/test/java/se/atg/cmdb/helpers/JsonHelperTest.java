package se.atg.cmdb.helpers;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.atg.cmdb.model.View;

public class JsonHelperTest {

  @Test
  public void entityToBsonWithJsonViews() {

    final WithExcludes expected = new WithExcludes();

    final ObjectMapper mapper = JsonHelper.configureObjectMapper(new ObjectMapper(), View.Api.class);
    final Document bson = JsonHelper.entityToBson(expected, mapper);

    Assert.assertNull(bson.getString("api"));
    Assert.assertEquals(expected.db, bson.getString("db"));
    Assert.assertEquals(expected.defaultView, bson.getString("defaultView"));
  }

  class WithExcludes {

    @JsonView(View.Api.class)
    public final String api = "API";
    @JsonView(View.Db.class)
    public final String db = "DB";
    public final String defaultView = "NO-VIEW";
  }
}
