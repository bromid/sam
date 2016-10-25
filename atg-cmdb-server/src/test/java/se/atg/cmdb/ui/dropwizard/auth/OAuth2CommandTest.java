package se.atg.cmdb.ui.dropwizard.auth;

import org.junit.BeforeClass;
import org.junit.Test;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.cmdb.auth.OAuth2Service;
import se.atg.cmdb.model.auth.OAuth2IdToken;
import se.atg.cmdb.ui.dropwizard.auth.OAuth2Command.Command;
import se.atg.cmdb.ui.dropwizard.configuration.CmdbConfiguration;
import se.atg.cmdb.ui.dropwizard.configuration.OAuthConfiguration;

public class OAuth2CommandTest {

  private static CmdbConfiguration configuration;
  private static OAuth2Service oAuth2Service;

  @BeforeClass
  public static void init() {

    configuration = new CmdbConfiguration() {{
      oauthConfig = new OAuthConfiguration() {{
        idTokenSignKey = "signKey";
        idTokenIssuer = "issuer";
      }};
    }};
    oAuth2Service = new OAuth2Service(configuration.getOAuthConfiguration());
  }

  @Test
  public void testCreate() throws Exception {

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.create,
      "subject", "test-user")
    );

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);
  }

  @Test
  public void testVerify() throws Exception {

    final OAuth2IdToken jwt = oAuth2Service.createIdToken("test-user");

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.verify,
      "jwt", jwt.token
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);
  }

  @Test
  public void testSign() throws Exception {

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.sign,
      "claims", "sub=test-user,scope=edit admin"
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);
  }

  @Test
  public void testSignWithExpiry() throws Exception {

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.sign,
      "claims", "sub=test-user,scope=edit admin,exp=1477913990"
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);
  }
}
