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
package org.jwcarman.slack.bolt.autoconfigure.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.TestRequests;

class ResponseUrlParameterBindingTest {

  private final ParameterBinding binding = new ResponseUrlParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("response_url=https://hooks.slack.com/1");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/1");
  }

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "response_url":"https://hooks.slack.com/2","actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/2");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "response_url":"https://hooks.slack.com/3"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/3");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req =
        TestRequests.dialogSubmission(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "response_url":"https://hooks.slack.com/4"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/4");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req =
        TestRequests.dialogCancellation(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "response_url":"https://hooks.slack.com/5"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/5");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "response_url":"https://hooks.slack.com/6"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("https://hooks.slack.com/6");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
