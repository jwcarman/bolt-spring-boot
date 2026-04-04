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

class UserNameParameterBindingTest {

  private final ParameterBinding binding = new UserNameParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = TestRequests.slashCommand("user_name=jsmith");
    assertThat(binding.resolve(req, null)).isEqualTo("jsmith");
  }

  @Test
  void extractsFromBlockAction() {
    // BlockActionPayload.User uses "username" field
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1","username":"jdoe"},"team":{"id":"T1"},"actions":[]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("jdoe");
  }

  @Test
  void extractsFromDialogSubmission() {
    // DialogSubmissionPayload.User uses "name" field
    var req =
        TestRequests.dialogSubmission(
            """
        {"user":{"id":"U1","name":"alice"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("alice");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req =
        TestRequests.dialogSuggestion(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("bob");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req =
        TestRequests.dialogCancellation(
            """
        {"user":{"id":"U1","name":"carol"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("carol");
  }

  @Test
  void extractsFromAttachmentAction() {
    // AttachmentActionPayload.User uses "name" field
    var req =
        TestRequests.attachmentAction(
            """
        {"user":{"id":"U1","name":"dave"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("dave");
  }

  @Test
  void extractsFromBlockSuggestion() {
    // BlockSuggestionPayload.User has both "name" and "username"; binding uses getName()
    var req =
        TestRequests.blockSuggestion(
            """
        {"user":{"id":"U1","name":"eve"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("eve");
  }

  @Test
  void extractsFromViewSubmission() {
    // ViewSubmissionPayload.User uses "name" field
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"frank"},"team":{"id":"T1"},\
        "view":{"state":{"values":{}}}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("frank");
  }

  @Test
  void extractsFromViewClosed() {
    var req =
        TestRequests.viewClosed(
            """
        {"user":{"id":"U1","name":"grace"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("grace");
  }

  @Test
  void extractsFromGlobalShortcut() {
    // GlobalShortcutPayload.User uses "username" field
    var req =
        TestRequests.globalShortcut(
            """
        {"user":{"id":"U1","username":"heidi"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("heidi");
  }

  @Test
  void extractsFromMessageShortcut() {
    // MessageShortcutPayload.User uses "name" field
    var req =
        TestRequests.messageShortcut(
            """
        {"user":{"id":"U1","name":"ivan"},"team":{"id":"T1"}}""");
    assertThat(binding.resolve(req, null)).isEqualTo("ivan");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
