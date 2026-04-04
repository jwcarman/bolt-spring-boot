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

class TeamIdParameterBindingTest {

  private final ParameterBinding binding = new TeamIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("team_id=T12345");
    assertThat(binding.resolve(req, null)).isEqualTo("T12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T67890"},"actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T67890");
  }

  @Test
  void extractsFromViewSubmission() {
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1"},"team":{"id":"T11111"},\
        "view":{"state":{"values":{}}}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T11111");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req =
        TestRequests.globalShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T22222"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T22222");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U1"},"team":{"id":"T33333"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T33333");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req =
        TestRequests.dialogSubmission(
            """
        {"user":{"id":"U1"},"team":{"id":"T44444"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T44444");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req =
        TestRequests.dialogSuggestion(
            """
        {"user":{"id":"U1"},"team":{"id":"T55000"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T55000");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req =
        TestRequests.dialogCancellation(
            """
        {"user":{"id":"U1"},"team":{"id":"T55555"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T55555");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T66666"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T66666");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req =
        TestRequests.blockSuggestion(
            """
        {"user":{"id":"U1"},"team":{"id":"T77777"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T77777");
  }

  @Test
  void extractsFromViewClosed() {
    var req =
        TestRequests.viewClosed(
            """
        {"user":{"id":"U1"},"team":{"id":"T88888"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("T88888");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
