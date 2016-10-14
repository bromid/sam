package se.atg.cmdb.model;

import java.security.Principal;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import se.atg.cmdb.ui.dropwizard.auth.Roles;
import se.atg.cmdb.ui.rest.Defaults;

public class User implements Principal {

  public String name;
  public final String[] roles = { Roles.READ, Roles.EDIT, Roles.ADMIN };

  public User(String name) {
    Validate.notBlank(name);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
  }
}
