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

class UserIdParameterBindingTest {

  private final ParameterBinding binding = new UserIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("user_id=U12345");
    assertThat(binding.resolve(req, null)).isEqualTo("U12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U67890"},"team":{"id":"T1"},"actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U67890");
  }

  @Test
  void extractsFromViewSubmission() {
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U11111"},"team":{"id":"T1"},"view":{"state":{"values":{}}}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U11111");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req =
        TestRequests.globalShortcut(
            """
        {"user":{"id":"U22222"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U22222");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U33333"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U33333");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req =
        TestRequests.dialogSubmission(
            """
        {"user":{"id":"U44444"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U44444");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req =
        TestRequests.dialogSuggestion(
            """
        {"user":{"id":"U55555"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U55555");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req =
        TestRequests.dialogCancellation(
            """
        {"user":{"id":"U66666"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U66666");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U77777"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U77777");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req =
        TestRequests.blockSuggestion(
            """
        {"user":{"id":"U88888"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U88888");
  }

  @Test
  void extractsFromViewClosed() {
    var req =
        TestRequests.viewClosed(
            """
        {"user":{"id":"U99999"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("U99999");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
