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
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import com.slack.api.model.view.ViewState;

public class ValueExtractor {

  public static Object extract(ViewState.Value value) {
    return switch (value.getType()) {
      case "plain_text_input" -> value.getValue();
      case "datepicker" -> value.getSelectedDate();
      case "timepicker" -> value.getSelectedTime();
      case "static_select" -> value.getSelectedOption().getValue();
      case "multi_static_select" ->
          value.getSelectedOptions().stream().map(ViewState.SelectedOption::getValue).toList();
      case "users_select" -> value.getSelectedUser();
      case "multi_users_select" -> value.getSelectedUsers();
      case "conversations_select" -> value.getSelectedConversation();
      case "channels_select" -> value.getSelectedChannel();
      case "rich_text_input" -> value.getRichTextValue();
      case "file_input" -> value.getFiles();
      default -> throw new IllegalArgumentException("Unknown input type: " + value.getType());
    };
  }
}
