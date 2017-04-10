package se.atg.sam.model.auth;

import java.security.Principal;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.sam.ui.dropwizard.auth.Roles;
import se.atg.sam.ui.rest.Defaults;

public class User implements Principal {

  @JsonProperty
  public String name;

  @JsonProperty
  public Optional<String> email;

  @JsonProperty
  public final String[] roles = { Roles.READ, Roles.EDIT, Roles.ADMIN };

  public User(
    @JsonProperty("name") String name,
    @JsonProperty("email") Optional<String> email
  ) {
    Validate.notBlank(name);
    Validate.notNull(email);
    this.name = name;
    this.email = email;
  }

  @Override
  public String getName() {
    return name;
  }

  public Optional<String> getEmail() {
    return email;
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
  }
}
