package se.atg.cmdb.ui.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import se.atg.cmdb.ui.rest.serializer.LinkSerializer;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

	private ObjectMapper objectMapper;

	public ObjectMapperProvider() {

		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new SimpleModule() {
			private static final long serialVersionUID = 1L;
			{addSerializer(new LinkSerializer());}
		});
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return objectMapper;
	}
}
