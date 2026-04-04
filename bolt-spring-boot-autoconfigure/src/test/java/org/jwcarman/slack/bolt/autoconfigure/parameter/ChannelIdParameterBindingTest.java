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

class ChannelIdParameterBindingTest {

  private final ParameterBinding binding = new ChannelIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("channel_id=C12345");
    assertThat(binding.resolve(req, null)).isEqualTo("C12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C67890"},"actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C67890");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C11111"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C11111");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req =
        TestRequests.dialogSubmission(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C22222"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C22222");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req =
        TestRequests.dialogSuggestion(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C33333"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C33333");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req =
        TestRequests.dialogCancellation(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C44444"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C44444");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C55555"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C55555");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req =
        TestRequests.blockSuggestion(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},"channel":{"id":"C66666"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("C66666");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
