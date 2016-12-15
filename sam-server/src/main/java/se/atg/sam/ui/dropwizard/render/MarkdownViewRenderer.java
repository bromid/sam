package se.atg.sam.ui.dropwizard.render;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import io.dropwizard.views.View;

public class MarkdownViewRenderer extends AbstractViewRenderer {

  private final PegDownProcessor processor;

  public MarkdownViewRenderer() {
    this(new PegDownProcessor(Extensions.ALL & ~Extensions.ANCHORLINKS));
  }

  public MarkdownViewRenderer(PegDownProcessor processor) {
    this.processor = processor;
  }

  @Override
  public void render(View view, Locale locale, OutputStream output) throws IOException {
    writeOutput(view, output, processor::markdownToHtml);
  }

  @Override
  public String getSuffix() {
    return ".md";
  }
}
