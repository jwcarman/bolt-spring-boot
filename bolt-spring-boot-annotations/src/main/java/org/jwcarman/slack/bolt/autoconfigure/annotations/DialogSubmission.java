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

/**
 * Marks a method as a Slack dialog submission handler, registered with {@link
 * com.slack.api.bolt.App#dialogSubmission(String,
 * com.slack.api.bolt.handler.builtin.DialogSubmissionHandler)}.
 *
 * <p>The method must accept {@code (DialogSubmissionRequest, DialogSubmissionContext)} and return
 * {@code Response}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DialogSubmission {
  /**
   * The callback ID to handle.
   *
   * @return the callback ID
   */
  String value();
}
