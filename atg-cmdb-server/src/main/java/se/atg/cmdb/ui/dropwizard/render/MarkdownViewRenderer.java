package se.atg.cmdb.ui.dropwizard.render;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

public class MarkdownViewRenderer implements ViewRenderer {

	private final LoadingCache<View, String> templateCache;
	private final PegDownProcessor processor;

	public MarkdownViewRenderer() {
		this(new PegDownProcessor(Extensions.ALL & ~Extensions.ANCHORLINKS));
	}

	public MarkdownViewRenderer(PegDownProcessor processor) {
		this.processor = processor;
		this.templateCache = CacheBuilder.newBuilder().build(new CacheLoader<View, String>() {

			@Override
			public String load(View key) throws Exception {

				final InputStream is = getClass().getResourceAsStream(key.getTemplateName());
				if (is == null) {
					throw new FileNotFoundException("Template " + key.getTemplateName() + " not found");
				}
				try (
					final StringWriter writer = new StringWriter()
				) {
					IOUtils.copy(is, writer, StandardCharsets.UTF_8);
					return writer.toString();
				}
			}
		});
	}

	@Override
	public boolean isRenderable(View view) {
		return view.getTemplateName().endsWith(".md");
	}

	@Override
	public void render(View view, Locale locale, OutputStream output) throws IOException {
		try {
			final String template = templateCache.get(view);
			try (
				final Writer writer = new PrintWriter(output);
			) {
				writer.write(processor.markdownToHtml(template));
			}
		} catch (ExecutionException exc) {
			if (FileNotFoundException.class.equals(exc.getCause().getClass())) {
				throw new FileNotFoundException(view.getTemplateName());
			}
			throw new IOException(exc);
		}
	}

	@Override
	public void configure(Map<String, String> map) {}

	@Override
	public String getSuffix() {
		return ".md";
	}
}
