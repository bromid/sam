package se.atg.cmdb.ui.dropwizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Link;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.ViewRenderer;
import io.dropwizard.views.mustache.MustacheViewRenderer;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import se.atg.cmdb.auth.OAuth2Service;
import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.User;
import se.atg.cmdb.model.View;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthenticator;
import se.atg.cmdb.ui.dropwizard.auth.BasicAuthorizer;
import se.atg.cmdb.ui.dropwizard.auth.IdTokenAuthenticator;
import se.atg.cmdb.ui.dropwizard.auth.OAuth2Command;
import se.atg.cmdb.ui.dropwizard.configuration.CmdbConfiguration;
import se.atg.cmdb.ui.dropwizard.configuration.OAuthConfiguration;
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

    final List<ViewRenderer> renderers = Arrays.asList(new MarkdownViewRenderer(), new HtmlViewRenderer(), new MustacheViewRenderer());
    bootstrap.addBundle(new ViewBundle<CmdbConfiguration>(renderers));
    bootstrap.addBundle(new AssetsBundle("/static", "/static", "index.mustache", "static"));
    bootstrap.addBundle(new AssetsBundle("/docs", "/docs", "index.html", "docs"));

    bootstrap.addCommand(new OAuth2Command());
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

    // OAuth2 service
    final OAuthConfiguration oAuthConfiguration = configuration.getOAuthConfiguration();
    final OAuth2Service oauth2Service = new OAuth2Service(oAuthConfiguration);

    // REST Resources
    environment.jersey().register(new InfoResource());
    environment.jersey().register(new IndexResource(objectMapper, oAuthConfiguration));
    environment.jersey().register(new OAuth2Resource(restClient, oauth2Service, oAuthConfiguration));
    environment.jersey().register(new ServerResource(database, objectMapper));
    environment.jersey().register(new ApplicationResource(database, objectMapper));
    environment.jersey().register(new GroupResource(database, objectMapper));
    environment.jersey().register(new AssetResource(database, objectMapper));
    environment.jersey().register(new SearchResource(database));

    // Authentication
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthDynamicFeature(
      new ChainedAuthFilter<>(
       ImmutableList.of(
        new BasicCredentialAuthFilter.Builder<User>()
          .setAuthenticator(new CachingAuthenticator<>(
            environment.metrics(),
            new BasicAuthenticator(),
            configuration.getAuthenticationCachePolicy()
          ))
          .setAuthorizer(new BasicAuthorizer())
          .setPrefix("Basic")
          .buildAuthFilter(),
        new OAuthCredentialAuthFilter.Builder<User>()
          .setAuthenticator(new CachingAuthenticator<>(
            environment.metrics(),
            new IdTokenAuthenticator(oauth2Service),
            configuration.getAuthenticationCachePolicy()
          ))
          .setAuthorizer(new BasicAuthorizer())
          .setPrefix("Bearer")
          .buildAuthFilter()
        )
      )
    ));

    // Core resources
    environment.jersey().register(DeclarativeLinkingFeature.class);
    environment.jersey().register(MongoServerExceptionMapper.class);

    // Init Swagger
    environment.jersey().register(ApiListingResource.class);
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
    config.setTitle("ATG SAM - Simple Asset Management");
    config.setVersion(getClass().getPackage().getImplementationVersion());
    config.setResourcePackage("se.atg.cmdb.ui.rest");
    config.setScan();
  }

  private static Client createRestClient(Environment environment, CmdbConfiguration configuration, String name) {

    final JerseyClientBuilder builder = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration());
    if (configuration.isLogRequests()) {
      builder.withProvider(new LoggingFilter(HTTP_LOGGER, true));
    }
    return builder.build(name);
  }
}
