package se.atg.cmdb.ui.dropwizard.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth0.jwt.JWTVerifyException;
import com.google.common.base.Splitter;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import se.atg.cmdb.auth.OAuth2Service;
import se.atg.cmdb.model.auth.OAuth2IdToken;
import se.atg.cmdb.ui.dropwizard.configuration.CmdbConfiguration;

import static se.atg.cmdb.auth.OAuth2Service.JWT_EXPIRY;
import static se.atg.cmdb.auth.OAuth2Service.JWT_NOT_VALID_BEFORE;
import static se.atg.cmdb.auth.OAuth2Service.JWT_ISSUED_AT;

public class OAuth2Command extends ConfiguredCommand<CmdbConfiguration> {

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
  protected void run(Bootstrap<CmdbConfiguration> bootstrap, Namespace namespace, CmdbConfiguration configuration) throws Exception {

    final OAuth2Service service = new OAuth2Service(configuration.getOAuthConfiguration());
    final Command command = namespace.get("subcommand");
    switch (command) {
      case create:
        create(namespace.getString("subject"), service);
        break;
      case verify:
        verify(namespace.getString("jwt"), service);
        break;
      case sign:
        sign(namespace.getString("claims"), service);
        break;
      default:
        throw new IllegalStateException("Unknown command");
    }
  }

  private void create(String subject, OAuth2Service service) {
    final OAuth2IdToken token = service.createIdToken(subject);
    System.out.println("JSON Web Token for (" + subject + "): " + token.token);
  }

  private void sign(String params, OAuth2Service service) {

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

    final OAuth2IdToken token = service.sign(claims);
    System.out.println("JSON Web Token: " + token.token);
  }

  private void verify(String jwt, OAuth2Service service) {
    try {
      final Map<String, Object> claims = service.verify(jwt);
      System.out.println("Valid JSON Web Token");
      System.out.println(claims);
    } catch (RuntimeException | GeneralSecurityException | IOException exc) {
      System.err.println("Failed to verify the token");
      exc.printStackTrace();
    } catch (JWTVerifyException exc) {
      System.out.println("The token is not valid: " + exc.getLocalizedMessage());
    }
  }

  enum Command {
    create, verify, sign;
  }
}
