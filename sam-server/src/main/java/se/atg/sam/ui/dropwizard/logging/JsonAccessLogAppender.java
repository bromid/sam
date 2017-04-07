package se.atg.sam.ui.dropwizard.logging;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.AbstractPatternJsonProvider;
import net.logstash.logback.composite.accessevent.AccessEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.accessevent.AccessEventPatternJsonProvider;
import net.logstash.logback.composite.accessevent.ContentLengthJsonProvider;
import net.logstash.logback.composite.accessevent.ElapsedTimeJsonProvider;
import net.logstash.logback.composite.accessevent.MethodJsonProvider;
import net.logstash.logback.composite.accessevent.ProtocolJsonProvider;
import net.logstash.logback.composite.accessevent.RemoteHostJsonProvider;
import net.logstash.logback.composite.accessevent.RemoteUserJsonProvider;
import net.logstash.logback.composite.accessevent.RequestedUriJsonProvider;
import net.logstash.logback.composite.accessevent.StatusCodeJsonProvider;
import net.logstash.logback.encoder.AccessEventCompositeJsonEncoder;

@JsonTypeName("json-access")
public class JsonAccessLogAppender extends AbstractAppenderFactory<IAccessEvent> {

  public Appender<IAccessEvent> build(
      LoggerContext context,
      String applicationName,
      LayoutFactory<IAccessEvent> layoutFactory,
      LevelFilterFactory<IAccessEvent> levelFilterFactory,
      AsyncAppenderFactory<IAccessEvent> asyncAppenderFactory
    ) {

    final AccessEventCompositeJsonEncoder encoder = new AccessEventCompositeJsonEncoder();
    encoder.setContext(context);

    encoder.getProviders().addProvider(withPattern("{ \"type\": \"access\" }", new AccessEventPatternJsonProvider(), context));
    encoder.getProviders().addProvider(withName("timestamp" ,new AccessEventFormattedTimestampJsonProvider(), context));
    encoder.getProviders().addProvider(withName("status", new StatusCodeJsonProvider(), context));
    encoder.getProviders().addProvider(withName("method", new MethodJsonProvider(), context));
    encoder.getProviders().addProvider(withName("protocol", new ProtocolJsonProvider(), context));
    encoder.getProviders().addProvider(withName("uri", new RequestedUriJsonProvider(), context));
    encoder.getProviders().addProvider(withName("client-ip", new RemoteHostJsonProvider(), context));
    encoder.getProviders().addProvider(withName("user", new RemoteUserJsonProvider(), context));
    encoder.getProviders().addProvider(withName("elapsed-time", new ElapsedTimeJsonProvider(), context));
    encoder.getProviders().addProvider(withName("content-length", new ContentLengthJsonProvider(), context));
    encoder.getProviders().addProvider(withPattern("{ \"user-agent\": \"%i{User-Agent}\" }", new AccessEventPatternJsonProvider(), context));
    encoder.start();

    final ConsoleAppender<IAccessEvent> appender = new ConsoleAppender<>();
    appender.setContext(context);
    appender.setEncoder(encoder);
    appender.start();

    return wrapAsync(appender, asyncAppenderFactory);
  }

  private AbstractPatternJsonProvider<IAccessEvent> withPattern(
      String pattern,
      AbstractPatternJsonProvider<IAccessEvent> provider,
      LoggerContext context
    ) {

    provider.setContext(context);
    provider.setPattern(pattern);
    return provider;
  }

  private <T extends DeferredProcessingAware> AbstractFieldJsonProvider<T> withName(
      String name,
      AbstractFieldJsonProvider<T> provider,
      LoggerContext context
    ) {

    provider.setContext(context);
    provider.setFieldName(name);
    return provider;
  }
}
