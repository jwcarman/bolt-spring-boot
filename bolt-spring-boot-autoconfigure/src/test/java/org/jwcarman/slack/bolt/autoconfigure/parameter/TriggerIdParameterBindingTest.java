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

class TriggerIdParameterBindingTest {

  private final ParameterBinding binding = new TriggerIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("trigger_id=tr12345");
    assertThat(binding.resolve(req, null)).isEqualTo("tr12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "trigger_id":"tr67890","actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("tr67890");
  }

  @Test
  void extractsFromViewSubmission() {
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "trigger_id":"tr11111","view":{"state":{"values":{}}}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("tr11111");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req =
        TestRequests.globalShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "trigger_id":"tr22222"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("tr22222");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "trigger_id":"tr33333"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("tr33333");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "trigger_id":"tr44444"}""");
    assertThat(binding.resolve(req, null)).isEqualTo("tr44444");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
