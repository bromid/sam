package se.atg.sam.ui.dropwizard.logging;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.AbstractPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;

@JsonTypeName("json")
public class JsonLogAppender extends AbstractAppenderFactory<ILoggingEvent> {

  public Appender<ILoggingEvent> build(
      LoggerContext context,
      String applicationName,
      LayoutFactory<ILoggingEvent> layoutFactory,
      LevelFilterFactory<ILoggingEvent> levelFilterFactory,
      AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory
    ) {

    final LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
    encoder.setContext(context);

    encoder.getProviders().addProvider(withPattern("{ \"type\": \"system\" }", new LoggingEventPatternJsonProvider(), context));
    encoder.getProviders().addProvider(withName("timestamp", new LoggingEventFormattedTimestampJsonProvider(), context));
    encoder.getProviders().addProvider(new LogLevelJsonProvider() );
    encoder.getProviders().addProvider(new MessageJsonProvider() );
    encoder.getProviders().addProvider(new StackTraceJsonProvider() );

    encoder.start();

    final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setContext(context);
    appender.setEncoder(encoder);
    appender.start();

    return wrapAsync(appender, asyncAppenderFactory);
  }

  private AbstractPatternJsonProvider<ILoggingEvent> withPattern(
      String pattern,
      AbstractPatternJsonProvider<ILoggingEvent> provider,
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
