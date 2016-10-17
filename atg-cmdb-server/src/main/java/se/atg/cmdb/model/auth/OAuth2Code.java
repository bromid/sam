package se.atg.cmdb.model.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "OAuth2 code")
public class OAuth2Code {

  public String code;
  public String state;

  public OAuth2Code() {}

  public OAuth2Code(String code, String state) {
    this.code = code;
    this.state = state;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, Defaults.STYLE);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
