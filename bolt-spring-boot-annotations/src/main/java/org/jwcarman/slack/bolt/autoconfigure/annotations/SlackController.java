/*
 * Copyright © 2026 James Carman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwcarman.slack.bolt.autoconfigure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Stereotype annotation for Spring beans that contain Slack Bolt handler methods.
 *
 * <p>This is a specialization of {@link org.springframework.stereotype.Component @Component}, so
 * annotated classes are automatically registered as Spring beans. Beans with this annotation are
 * scanned for handler methods like {@code @SlashCommand}, {@code @BlockAction}, etc.
 *
 * @see SlashCommand
 * @see Event
 * @see BlockAction
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SlackController {

  /**
   * The value may indicate a suggestion for a logical component name.
   *
   * @return the suggested component name, or empty string
   */
  @AliasFor(annotation = Component.class)
  String value() default "";
}
