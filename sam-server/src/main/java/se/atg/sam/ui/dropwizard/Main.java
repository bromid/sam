package se.atg.sam.ui.dropwizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
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
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
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
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.helpers.JsonHelper;
import se.atg.sam.model.View;
import se.atg.sam.model.auth.User;
import se.atg.sam.ui.dropwizard.auth.BasicAuthenticator;
import se.atg.sam.ui.dropwizard.auth.BasicAuthorizer;
import se.atg.sam.ui.dropwizard.auth.IdTokenAuthenticator;
import se.atg.sam.ui.dropwizard.command.AddTestdataCommand;
import se.atg.sam.ui.dropwizard.command.CreateDatabaseCommand;
import se.atg.sam.ui.dropwizard.command.OAuth2Command;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;
import se.atg.sam.ui.dropwizard.db.MongoDatabaseHealthCheck;
import se.atg.sam.ui.dropwizard.render.HtmlViewRenderer;
import se.atg.sam.ui.dropwizard.render.MarkdownViewRenderer;
import se.atg.sam.ui.rest.ApplicationResource;
import se.atg.sam.ui.rest.AssetResource;
import se.atg.sam.ui.rest.GroupResource;
import se.atg.sam.ui.rest.IndexResource;
import se.atg.sam.ui.rest.InfoResource;
import se.atg.sam.ui.rest.OAuth2Resource;
import se.atg.sam.ui.rest.SearchResource;
import se.atg.sam.ui.rest.ServerResource;
import se.atg.sam.ui.rest.mapper.ConstraintViolationExceptionMapper;
import se.atg.sam.ui.rest.mapper.MongoServerExceptionMapper;

public class Main extends Application<SamConfiguration> {

  private static final Logger HTTP_LOGGER = Logger.getLogger(LoggingFeature.class.getName());

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void initialize(Bootstrap<SamConfiguration> bootstrap) {

    final List<ViewRenderer> renderers = Arrays.asList(new MarkdownViewRenderer(), new HtmlViewRenderer(), new MustacheViewRenderer());
    bootstrap.addBundle(new ViewBundle<SamConfiguration>(renderers));
    bootstrap.addBundle(new AssetsBundle("/static", "/static", "index.mustache", "static"));
    bootstrap.addBundle(new AssetsBundle("/docs", "/docs", "index.html", "docs"));

    bootstrap.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(
        bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor()
      )
    );

    bootstrap.addCommand(new OAuth2Command());
    bootstrap.addCommand(new CreateDatabaseCommand(this));
    bootstrap.addCommand(new AddTestdataCommand(this));
  }

  @Override
  public void run(SamConfiguration configuration, Environment environment) throws Exception {

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
    environment.jersey().register(ConstraintViolationExceptionMapper.class);

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
    config.setResourcePackage("se.atg.sam.ui.rest");
    config.setScan();
  }

  private static Client createRestClient(Environment environment, SamConfiguration configuration, String name) {

    final JerseyClientBuilder builder = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration());
    if (configuration.isLogRequests()) {
      builder.withProvider(new LoggingFeature(HTTP_LOGGER, Level.INFO, Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
    }
    return builder.build(name);
  }
}
