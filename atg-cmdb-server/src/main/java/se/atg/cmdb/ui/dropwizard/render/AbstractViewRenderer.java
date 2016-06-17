package se.atg.cmdb.ui.dropwizard.render;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

public abstract class AbstractViewRenderer implements ViewRenderer {

  protected final LoadingCache<String, String> templateCache;

  public AbstractViewRenderer() {
    this.templateCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {

      @Override
      public String load(String key) throws Exception {
        try (final InputStream is = getClass().getResourceAsStream(key);) {
          if (is == null) {
            throw new FileNotFoundException("Template " + key + " not found");
          }
          final StringWriter writer = new StringWriter();
          IOUtils.copy(is, writer, StandardCharsets.UTF_8);
          return writer.toString();
        }
      }
    });
  }

  protected void writeOutput(View view, OutputStream output, Function<String,String> renderer) throws IOException {
    try (
      final Writer writer = new PrintWriter(output);
    ) {
      final String template = templateCache.get(view.getTemplateName());
      writer.write(renderer.apply(template));
    } catch (ExecutionException exc) {
      if (FileNotFoundException.class.equals(exc.getCause().getClass())) {
        throw new FileNotFoundException(view.getTemplateName());
      }
      throw new IOException(exc);
    }
  }

  @Override
  public boolean isRenderable(View view) {
    return view.getTemplateName().endsWith(getSuffix());
  }

  @Override
  public void configure(Map<String, String> map) {
  }
}
