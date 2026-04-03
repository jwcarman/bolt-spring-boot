package org.jwcarman.slack.bolt.autoconfigure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
  Class<? extends com.slack.api.model.event.Event> value();
}
