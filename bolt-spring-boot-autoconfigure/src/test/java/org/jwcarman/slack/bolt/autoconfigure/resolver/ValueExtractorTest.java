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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.slack.api.model.view.ViewState;

class ValueExtractorTest {

  @Test
  void extractsPlainTextInput() {
    ViewState.Value value = new ViewState.Value();
    value.setType("plain_text_input");
    value.setValue("hello");
    assertThat(ValueExtractor.extract(value)).isEqualTo("hello");
  }

  @Test
  void extractsDatePicker() {
    ViewState.Value value = new ViewState.Value();
    value.setType("datepicker");
    value.setSelectedDate("2026-04-03");
    assertThat(ValueExtractor.extract(value)).isEqualTo("2026-04-03");
  }

  @Test
  void extractsTimePicker() {
    ViewState.Value value = new ViewState.Value();
    value.setType("timepicker");
    value.setSelectedTime("14:30");
    assertThat(ValueExtractor.extract(value)).isEqualTo("14:30");
  }

  @Test
  void extractsStaticSelect() {
    ViewState.SelectedOption option = new ViewState.SelectedOption();
    option.setValue("high");
    ViewState.Value value = new ViewState.Value();
    value.setType("static_select");
    value.setSelectedOption(option);
    assertThat(ValueExtractor.extract(value)).isEqualTo("high");
  }

  @Test
  void extractsMultiStaticSelect() {
    ViewState.SelectedOption opt1 = new ViewState.SelectedOption();
    opt1.setValue("a");
    ViewState.SelectedOption opt2 = new ViewState.SelectedOption();
    opt2.setValue("b");
    ViewState.Value value = new ViewState.Value();
    value.setType("multi_static_select");
    value.setSelectedOptions(List.of(opt1, opt2));
    assertThat(ValueExtractor.extract(value)).isEqualTo(List.of("a", "b"));
  }

  @Test
  void extractsUsersSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("users_select");
    value.setSelectedUser("U12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("U12345");
  }

  @Test
  void extractsMultiUsersSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("multi_users_select");
    value.setSelectedUsers(List.of("U1", "U2"));
    assertThat(ValueExtractor.extract(value)).isEqualTo(List.of("U1", "U2"));
  }

  @Test
  void extractsConversationsSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("conversations_select");
    value.setSelectedConversation("C12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("C12345");
  }

  @Test
  void extractsChannelsSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("channels_select");
    value.setSelectedChannel("C12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("C12345");
  }

  @Test
  void throwsForUnknownType() {
    ViewState.Value value = new ViewState.Value();
    value.setType("unknown_widget");
    assertThatThrownBy(() -> ValueExtractor.extract(value))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("unknown_widget");
  }
}
