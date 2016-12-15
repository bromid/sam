package se.atg.sam.ui.dropwizard.render;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.function.Function;

import io.dropwizard.views.View;

public class HtmlViewRenderer extends AbstractViewRenderer {

  @Override
  public void render(View view, Locale locale, OutputStream output) throws IOException {
    writeOutput(view, output, Function.identity());
  }

  @Override
  public String getSuffix() {
    return ".html";
  }
}
