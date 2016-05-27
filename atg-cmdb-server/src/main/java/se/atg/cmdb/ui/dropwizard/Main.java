package se.atg.cmdb.ui.dropwizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import se.atg.cmdb.model.User;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthenticator;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthorizer;
import se.atg.cmdb.ui.dropwizard.db.MongoDBHealthCheck;
import se.atg.cmdb.ui.rest.ApplicationResource;
import se.atg.cmdb.ui.rest.GroupResource;
import se.atg.cmdb.ui.rest.ServerResource;
import se.atg.cmdb.ui.rest.mapper.MongoServerExceptionMapper;
import se.atg.cmdb.ui.rest.serializer.LinkSerializer;

public class Main extends Application<CMDBConfiguration> {

    public static void main(String[] args) throws Exception {
    	new Main().run(args);
    }

	@Override
	public void initialize(Bootstrap<CMDBConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/assets","/","index.html"));
	}

	@Override
	public void run(CMDBConfiguration configuration, Environment environment) throws Exception {

		// Healthchecks
		final MongoDatabase database = configuration.getDBConnectionFactory().getDatabase(environment);
		environment.healthChecks().register("mongoDB", new MongoDBHealthCheck(database));

		// Jackson configuration
		final ObjectMapper objectMapper = environment.getObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new SimpleModule() {
			private static final long serialVersionUID = 1L;
			{addSerializer(new LinkSerializer());}
		});

		// REST Resources
		environment.jersey().register(new ServerResource(database, objectMapper));
		environment.jersey().register(new ApplicationResource(database, objectMapper));
		environment.jersey().register(new GroupResource(database, objectMapper));

		// Authentication
		environment.jersey().register(RolesAllowedDynamicFeature.class);
	    environment.jersey().register(new AuthDynamicFeature(
	    	new BasicCredentialAuthFilter.Builder<User>()
	    		.setAuthenticator(new BasicAuthenticator())
	    		.setAuthorizer(new BasicAuthorizer())
	        	.setPrefix("Basic")
	    		.buildAuthFilter())
	    );

	    // Core resources
		environment.jersey().register(ApiListingResource.class);
		environment.jersey().register(DeclarativeLinkingFeature.class);
		environment.jersey().register(MongoServerExceptionMapper.class);

		// Init Swagger
		ModelConverters.getInstance().addConverter(new ModelConverter() {

			@Override
			public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {			
				return chain.next().resolveProperty(type, context, annotations, chain);
			}

			@Override
			public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {

				final JavaType javaType = objectMapper.constructType(type);
				if (javaType.isTypeOrSubTypeOf(Link.class)) {
					return new ModelImpl()
						.name("link")
						.type("object")
						.property("rel", new StringProperty())
						.property("href", new StringProperty(StringProperty.Format.URL));
				}
				return chain.next().resolve(type, context, chain);
			}
		});

		final BeanConfig config = new BeanConfig();
	    config.setTitle("ATG Configuration Management Database");
	    config.setVersion("1.0.0");
	    config.setResourcePackage("se.atg.cmdb.ui.rest");
	    config.setBasePath("services");
	    config.setScan(true);
	}
}