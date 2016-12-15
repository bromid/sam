package se.atg.sam.ui.rest.integrationtest.helpers;

import java.util.Optional;

import javax.ws.rs.client.WebTarget;

public abstract class RestHelper {

  public static WebTarget queryParam(WebTarget target, Optional<?> param, String name) {
   if (param.isPresent()) {
     return target.queryParam(name, param.get());
   }
   return target;
  }
}
