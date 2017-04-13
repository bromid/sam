package se.atg.sam.ui.dropwizard.command;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import net.sourceforge.argparse4j.inf.Namespace;
import se.atg.sam.auth.OAuth2Service;
import se.atg.sam.model.auth.OAuth2IdToken;
import se.atg.sam.ui.dropwizard.configuration.SamConfiguration;
import se.atg.sam.ui.dropwizard.command.OAuth2Command;
import se.atg.sam.ui.dropwizard.command.OAuth2Command.Command;
import se.atg.sam.ui.dropwizard.configuration.OAuthConfiguration;

public class OAuth2CommandTest {

  private ByteArrayOutputStream systemOutBuffer = new ByteArrayOutputStream();
  private SamConfiguration configuration;
  private OAuth2Service oAuth2Service;

  @Before
  public void init() {

    configuration = new SamConfiguration() {{
      oauthConfig = new OAuthConfiguration() {{
        idTokenSignKey = "signKey";
        idTokenIssuer = "issuer";
      }};
      sysOut = new PrintStream(systemOutBuffer);
    }};
    oAuth2Service = new OAuth2Service(configuration.getOAuthConfiguration());
  }

  @Test
  public void testCreate() throws Exception {

    final String subject = "test-user";

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.create,
      "subject", "test-user")
    );

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);

    final String jwt = getSystemOut().split(": ")[1];
    final Map<String, Object> claims = oAuth2Service.verify(jwt);
    Assert.assertEquals(subject, claims.get("sub"));
  }

  @Test
  public void testVerify() throws Exception {

    final String subject = "test-user";
    final OAuth2IdToken jwt = oAuth2Service.createIdToken(subject, Optional.empty());

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.verify,
      "jwt", jwt.token
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);

    final String claims = getSystemOut().split("\n")[1];
    Assert.assertTrue(claims.contains("sub=" + subject));
  }

  @Test
  public void testSign() throws Exception {

    final String subject = "test-user";
    final String roles = "edit admin";

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.sign,
      "claims", "sub=test-user,scope=edit admin"
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);

    final String jwt = getSystemOut().split(": ")[1];
    final Map<String, Object> claims = oAuth2Service.verify(jwt);
    Assert.assertEquals(roles, claims.get("scope"));
    Assert.assertEquals(subject, claims.get("sub"));
  }

  @Test
  public void testSignWithExpiry() throws Exception {

    final String subject = "test-user";
    final String roles = "edit admin";
    final long expiry = System.currentTimeMillis() + 60*1000;

    final Namespace args = new Namespace(ImmutableMap.of(
      "subcommand", Command.sign,
      "claims", "sub=" + subject + ",scope=" + roles + ",exp=" + expiry
    ));

    final OAuth2Command command = new OAuth2Command();
    command.run(null, args, configuration);

    final String jwt = getSystemOut().split(": ")[1];
    final Map<String, Object> claims = oAuth2Service.verify(jwt);
    Assert.assertEquals(expiry, claims.get("exp"));
    Assert.assertEquals(roles, claims.get("scope"));
    Assert.assertEquals(subject, claims.get("sub"));
  }

  private String getSystemOut() {
    return systemOutBuffer.toString(Charset.forName("UTF8"));
  }
}
