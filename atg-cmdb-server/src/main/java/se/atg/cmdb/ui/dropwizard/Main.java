package se.atg.cmdb.ui.dropwizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Link;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.User;
import se.atg.cmdb.model.View;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthenticator;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthorizer;
import se.atg.cmdb.ui.dropwizard.configuration.CmdbConfiguration;
import se.atg.cmdb.ui.dropwizard.db.MongoDatabaseHealthCheck;
import se.atg.cmdb.ui.dropwizard.render.HtmlViewRenderer;
import se.atg.cmdb.ui.dropwizard.render.MarkdownViewRenderer;
import se.atg.cmdb.ui.rest.ApplicationResource;
import se.atg.cmdb.ui.rest.AssetResource;
import se.atg.cmdb.ui.rest.GroupResource;
import se.atg.cmdb.ui.rest.IndexResource;
import se.atg.cmdb.ui.rest.InfoResource;
import se.atg.cmdb.ui.rest.OAuth2Resource;
import se.atg.cmdb.ui.rest.SearchResource;
import se.atg.cmdb.ui.rest.ServerResource;
import se.atg.cmdb.ui.rest.mapper.MongoServerExceptionMapper;

public class Main extends Application<CmdbConfiguration> {

  private static final java.util.logging.Logger HTTP_LOGGER = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void initialize(Bootstrap<CmdbConfiguration> bootstrap) {
    bootstrap.addBundle(new ViewBundle<CmdbConfiguration>(Arrays.asList(new MarkdownViewRenderer(), new HtmlViewRenderer())));
    bootstrap.addBundle(new AssetsBundle("/static", "/static", "index.html", "static"));
    bootstrap.addBundle(new AssetsBundle("/docs", "/docs", "index.html", "docs"));
  }

  @Override
  public void run(CmdbConfiguration configuration, Environment environment) throws Exception {

    // Healthchecks
    final MongoDatabase database = configuration.getDbConnectionFactory().getDatabase(environment.lifecycle());
    environment.healthChecks().register("mongoDB", new MongoDatabaseHealthCheck(database));

    // Jackson configuration
    final ObjectMapper objectMapper = JsonHelper.configureObjectMapper(environment.getObjectMapper(), View.Api.class);

    // REST Client
    final Client restClient = createRestClient(environment, configuration, "default_client");

    // REST Resources
    environment.jersey().register(new InfoResource());
    environment.jersey().register(new IndexResource());
    environment.jersey().register(new OAuth2Resource(restClient, configuration.getOAuthConfiguration()));
    environment.jersey().register(new ServerResource(database, objectMapper));
    environment.jersey().register(new ApplicationResource(database, objectMapper));
    environment.jersey().register(new GroupResource(database, objectMapper));
    environment.jersey().register(new AssetResource(database, objectMapper));
    environment.jersey().register(new SearchResource(database));

    // Authentication
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
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
    config.setVersion(getClass().getPackage().getImplementationVersion());
    config.setResourcePackage("se.atg.cmdb.ui.rest");
    config.setScan(true);
  }

  private static Client createRestClient(Environment environment, CmdbConfiguration configuration, String name) {

    final JerseyClientBuilder builder = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration());
    if (configuration.isLogRequests()) {
      builder.withProvider(new LoggingFilter(HTTP_LOGGER, true));
    }
    return builder.build(name);
  }
}
