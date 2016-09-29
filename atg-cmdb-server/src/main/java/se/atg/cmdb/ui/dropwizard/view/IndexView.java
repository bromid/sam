package se.atg.cmdb.ui.dropwizard.view;

import io.dropwizard.views.View;

public class IndexView extends View {

  private String settings;

  public IndexView(String settings) {
    super("/static/index.mustache");
    this.settings = settings;
  }

  public String getSettings() {
    return settings;
  }

  public static class Settings {
    public Oauth oauth;
  }

  public static class Oauth {
    public String url;
    public String clientId;
    public String origin;
  }
}
