package se.atg.sam.ui.dropwizard.command;

import static se.atg.sam.auth.OAuth2Service.JWT_EXPIRY;
import static se.atg.sam.auth.OAuth2Service.JWT_ISSUED_AT;
import static se.atg.sam.auth.OAuth2Service.JWT_NOT_VALID_BEFORE;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.JWTSigner.Options;
import com.google.common.base.Splitter;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.auth.OAuth2Service.JwtField;
import se.atg.sam.model.auth.OAuth2IdToken;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;

public class OAuth2Command extends ConfiguredCommand<SamConfiguration> {

  private static final Options JWT_OPTIONS = new Options().setIssuedAt(true).setJwtId(true);

  public OAuth2Command() {
    super("oauth2", "Utility for handling OAuth2 tokens");
  }

  @Override
  public void configure(Subparser parser) {

    final Subparsers subparsers = parser.addSubparsers();

    final Subparser createParser = subparsers.addParser("create")
      .setDefault("subcommand", Command.create)
      .help("Create JSON Web Token for a subject");
    createParser.addArgument("subject").help("Name of the subject");
    super.configure(createParser);

    final Subparser verifyParser = subparsers.addParser("verify")
      .setDefault("subcommand", Command.verify)
      .help("Verify a JSON Web Token ");
    verifyParser.addArgument("jwt").help("The token to verify");
    super.configure(verifyParser);

    final Subparser signParser = subparsers.addParser("sign")
      .setDefault("subcommand", Command.sign)
      .help("Sign a JSON Web Token ");
    signParser.addArgument("claims").help("List of claims to sign i.e. (sub=user,scope=user admin)");
    super.configure(signParser);
  }

  @Override
  protected void run(Bootstrap<SamConfiguration> bootstrap, Namespace namespace, SamConfiguration configuration) throws Exception {

    final OAuth2Service service = new OAuth2Service(configuration.getOAuthConfiguration());
    final Command command = namespace.get("subcommand");
    switch (command) {
      case create:
        create(namespace.getString("subject"), service, configuration);
        break;
      case verify:
        verify(namespace.getString("jwt"), service, configuration);
        break;
      case sign:
        sign(namespace.getString("claims"), service, configuration);
        break;
      default:
        throw new IllegalStateException("Unknown command");
    }
  }

  private void create(String subject, OAuth2Service service, SamConfiguration configuration) {
    final OAuth2IdToken token = service.createIdToken(subject);
    configuration.getSystemOut().println("JSON Web Token for (" + subject + "): " + token.token);
  }

  private void sign(String params, OAuth2Service service, SamConfiguration configuration) {

    final Map<String, Object> claims = Splitter
      .on(',')
      .withKeyValueSeparator('=')
      .split(params)
      .entrySet().stream()
      .collect(Collectors.toMap(
        (entry) -> entry.getKey(),
        (entry) -> {
          final String value = entry.getValue();
          switch (entry.getKey()) {
            case JWT_EXPIRY:
            case JWT_NOT_VALID_BEFORE:
            case JWT_ISSUED_AT:
              return Long.parseLong(value);
            default:
              return value;
          }
        }
      ));

    final OAuthConfiguration oauthConfig = configuration.getOAuthConfiguration();
    claims.put(JwtField.issuer.id, oauthConfig.getIdTokenIssuer());
    claims.put(JwtField.audience.id, oauthConfig.getIdTokenIssuer());

    final OAuth2IdToken token = service.sign(claims, JWT_OPTIONS);
    configuration.getSystemOut().println("JSON Web Token: " + token.token);
  }

  private void verify(String jwt, OAuth2Service service, SamConfiguration configuration) {
    try {
      final Map<String, Object> claims = service.verify(jwt);
      configuration.getSystemOut().println("Valid JSON Web Token");
      configuration.getSystemOut().println(claims);
    } catch (RuntimeException | GeneralSecurityException | IOException exc) {
      configuration.getSystemErr().println("Failed to verify the token");
      exc.printStackTrace();
    } catch (JWTVerifyException exc) {
      configuration.getSystemOut().println("The token is not valid: " + exc.getLocalizedMessage());
    }
  }

  enum Command {
    create, verify, sign;
  }
}
