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
package org.jwcarman.slack.bolt.autoconfigure;

import java.util.Map;

import com.slack.api.bolt.request.RequestHeaders;
import com.slack.api.bolt.request.builtin.AttachmentActionRequest;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.BlockSuggestionRequest;
import com.slack.api.bolt.request.builtin.DialogCancellationRequest;
import com.slack.api.bolt.request.builtin.DialogSubmissionRequest;
import com.slack.api.bolt.request.builtin.DialogSuggestionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

/**
 * Factory methods for constructing real Bolt SDK request objects in tests. These avoid the need for
 * Mockito mocks of Bolt classes, which fail without the byte-buddy javaagent in IntelliJ's test
 * runner.
 */
public final class TestRequests {

  private static final RequestHeaders EMPTY_HEADERS = new RequestHeaders(Map.of());

  private TestRequests() {
    // Utility class
  }

  // ---- SlashCommand (URL-encoded form body) ----

  public static SlashCommandRequest slashCommand(String formBody) {
    return new SlashCommandRequest(formBody, EMPTY_HEADERS);
  }

  public static SlashCommandRequest slashCommand(
      String userId,
      String userName,
      String teamId,
      String channelId,
      String triggerId,
      String responseUrl,
      String text) {
    var body =
        "user_id="
            + userId
            + "&user_name="
            + userName
            + "&team_id="
            + teamId
            + "&channel_id="
            + channelId
            + "&trigger_id="
            + triggerId
            + "&response_url="
            + responseUrl
            + "&text="
            + text;
    return slashCommand(body);
  }

  // ---- BlockAction (JSON body) ----

  public static BlockActionRequest blockAction(String json) {
    return new BlockActionRequest(json, json, EMPTY_HEADERS);
  }

  public static BlockActionRequest blockAction(
      String userId,
      String username,
      String teamId,
      String channelId,
      String triggerId,
      String responseUrl,
      String actionValue) {
    var json =
        """
        {"user":{"id":"%s","username":"%s"},"team":{"id":"%s"},"channel":{"id":"%s"},\
        "trigger_id":"%s","response_url":"%s",\
        "actions":[{"action_id":"btn","value":"%s"}]}"""
            .formatted(userId, username, teamId, channelId, triggerId, responseUrl, actionValue);
    return blockAction(json);
  }

  // ---- ViewSubmission (JSON body) ----

  public static ViewSubmissionRequest viewSubmission(String json) {
    return new ViewSubmissionRequest(json, json, EMPTY_HEADERS);
  }

  public static ViewSubmissionRequest viewSubmission(
      String userId, String userName, String teamId, String triggerId) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "trigger_id":"%s","view":{"state":{"values":{}}}}"""
            .formatted(userId, userName, teamId, triggerId);
    return viewSubmission(json);
  }

  // ---- ViewClosed (JSON body) ----

  public static ViewClosedRequest viewClosed(String json) {
    return new ViewClosedRequest(json, json, EMPTY_HEADERS);
  }

  public static ViewClosedRequest viewClosed(String userId, String userName, String teamId) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "view":{"state":{"values":{}}}}"""
            .formatted(userId, userName, teamId);
    return viewClosed(json);
  }

  // ---- GlobalShortcut (JSON body) ----

  public static GlobalShortcutRequest globalShortcut(String json) {
    return new GlobalShortcutRequest(json, json, EMPTY_HEADERS);
  }

  public static GlobalShortcutRequest globalShortcut(
      String userId, String username, String teamId, String triggerId) {
    var json =
        """
        {"user":{"id":"%s","username":"%s"},"team":{"id":"%s"},\
        "trigger_id":"%s"}"""
            .formatted(userId, username, teamId, triggerId);
    return globalShortcut(json);
  }

  // ---- MessageShortcut (JSON body) ----

  public static MessageShortcutRequest messageShortcut(String json) {
    return new MessageShortcutRequest(json, json, EMPTY_HEADERS);
  }

  public static MessageShortcutRequest messageShortcut(
      String userId,
      String userName,
      String teamId,
      String channelId,
      String triggerId,
      String responseUrl) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"},"trigger_id":"%s","response_url":"%s"}"""
            .formatted(userId, userName, teamId, channelId, triggerId, responseUrl);
    return messageShortcut(json);
  }

  // ---- DialogSubmission (JSON body) ----

  public static DialogSubmissionRequest dialogSubmission(String json) {
    return new DialogSubmissionRequest(json, json, EMPTY_HEADERS);
  }

  public static DialogSubmissionRequest dialogSubmission(
      String userId, String userName, String teamId, String channelId, String responseUrl) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"},"response_url":"%s"}"""
            .formatted(userId, userName, teamId, channelId, responseUrl);
    return dialogSubmission(json);
  }

  // ---- DialogSuggestion (JSON body) ----

  public static DialogSuggestionRequest dialogSuggestion(String json) {
    return new DialogSuggestionRequest(json, json, EMPTY_HEADERS);
  }

  public static DialogSuggestionRequest dialogSuggestion(
      String userId, String userName, String teamId, String channelId) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"}}"""
            .formatted(userId, userName, teamId, channelId);
    return dialogSuggestion(json);
  }

  // ---- DialogCancellation (JSON body) ----

  public static DialogCancellationRequest dialogCancellation(String json) {
    return new DialogCancellationRequest(json, json, EMPTY_HEADERS);
  }

  public static DialogCancellationRequest dialogCancellation(
      String userId, String userName, String teamId, String channelId, String responseUrl) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"},"response_url":"%s"}"""
            .formatted(userId, userName, teamId, channelId, responseUrl);
    return dialogCancellation(json);
  }

  // ---- AttachmentAction (JSON body) ----

  public static AttachmentActionRequest attachmentAction(String json) {
    return new AttachmentActionRequest(json, json, EMPTY_HEADERS);
  }

  public static AttachmentActionRequest attachmentAction(
      String userId,
      String userName,
      String teamId,
      String channelId,
      String triggerId,
      String responseUrl) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"},"trigger_id":"%s","response_url":"%s"}"""
            .formatted(userId, userName, teamId, channelId, triggerId, responseUrl);
    return attachmentAction(json);
  }

  // ---- BlockSuggestion (JSON body) ----

  public static BlockSuggestionRequest blockSuggestion(String json) {
    return new BlockSuggestionRequest(json, json, EMPTY_HEADERS);
  }

  public static BlockSuggestionRequest blockSuggestion(
      String userId, String userName, String teamId, String channelId) {
    var json =
        """
        {"user":{"id":"%s","name":"%s"},"team":{"id":"%s"},\
        "channel":{"id":"%s"}}"""
            .formatted(userId, userName, teamId, channelId);
    return blockSuggestion(json);
  }
}
