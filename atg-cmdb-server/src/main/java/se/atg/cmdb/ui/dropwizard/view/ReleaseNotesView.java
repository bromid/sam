package se.atg.cmdb.ui.dropwizard.view;

import io.dropwizard.views.View;

public class ReleaseNotesView extends View {
    public ReleaseNotesView() {
        super("/CHANGELOG.md");
    }
}
