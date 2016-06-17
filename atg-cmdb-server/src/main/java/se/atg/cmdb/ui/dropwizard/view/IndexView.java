package se.atg.cmdb.ui.dropwizard.view;

import io.dropwizard.views.View;

public class IndexView extends View {
  public IndexView() {
    super("/static/index.html");
  }
}
