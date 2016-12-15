package se.atg.sam.ui.dropwizard.view;

import io.dropwizard.views.View;

public class ReleaseNotesView extends View {
  public ReleaseNotesView() {
    super("/CHANGELOG.md");
  }
}
