package se.atg.cmdb.helpers;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.atg.cmdb.model.View;

public class JSONHelperTest {

	@Test
	public void entityToBsonWithJsonViews() {

		final WithExcludes expected = new WithExcludes();

		final ObjectMapper mapper = JSONHelper.configureObjectMapper(new ObjectMapper(), View.API.class);
		final Document bson = JSONHelper.entityToBson(expected, mapper);

		Assert.assertNull(bson.getString("api"));
		Assert.assertEquals(expected.db, bson.getString("db"));
		Assert.assertEquals(expected.defaultView, bson.getString("defaultView"));
	}

	class WithExcludes {

		@JsonView(View.API.class)
		public final String api = "API";
		@JsonView(View.DB.class)
		public final String db = "DB";
		public final String defaultView = "NO-VIEW";
	}
}
