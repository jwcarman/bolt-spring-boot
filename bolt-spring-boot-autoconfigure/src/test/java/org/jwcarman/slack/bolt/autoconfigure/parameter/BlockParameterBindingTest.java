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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.TestRequests;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionId;
import org.springframework.core.convert.support.DefaultConversionService;

import com.slack.api.model.File;
import com.slack.api.model.block.RichTextBlock;

class BlockParameterBindingTest {

  private final DefaultConversionService conversionService = new DefaultConversionService();

  public record SimpleForm(String title, String description) {}

  @Test
  void bindsRecordFromViewState() {
    var binding = new BlockParameterBinding("simple-form", SimpleForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "simple-form":{"title":{"type":"plain_text_input","value":"My Title"},"description":{"type":"plain_text_input","value":"My Desc"}}
        }}}}""");

    var form = (SimpleForm) binding.resolve(req, null);
    assertThat(form.title()).isEqualTo("My Title");
    assertThat(form.description()).isEqualTo("My Desc");
  }

  @Test
  void bindsWithConventionBasedBlockName() {
    // Java name "simpleForm" should match "simple-form" via kebab-case
    var binding = new BlockParameterBinding("simpleForm", SimpleForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "simple-form":{"title":{"type":"plain_text_input","value":"Kebab"},"description":{"type":"plain_text_input","value":"Match"}}
        }}}}""");

    var form = (SimpleForm) binding.resolve(req, null);
    assertThat(form.title()).isEqualTo("Kebab");
  }

  public record TypedForm(String name, Integer age) {}

  @Test
  void convertsTypesViaConversionService() {
    var binding = new BlockParameterBinding("typed-form", TypedForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "typed-form":{"name":{"type":"plain_text_input","value":"James"},"age":{"type":"plain_text_input","value":"42"}}
        }}}}""");

    var form = (TypedForm) binding.resolve(req, null);
    assertThat(form.name()).isEqualTo("James");
    assertThat(form.age()).isEqualTo(42);
  }

  @Test
  void handlesDatePicker() {
    var binding = new BlockParameterBinding("date-block", DateForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "date-block":{"selected":{"type":"datepicker","selected_date":"2026-04-04"}}
        }}}}""");

    var form = (DateForm) binding.resolve(req, null);
    assertThat(form.selected()).isEqualTo("2026-04-04");
  }

  public record DateForm(String selected) {}

  @Test
  void handlesStaticSelect() {
    var binding = new BlockParameterBinding("select-block", SelectForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "select-block":{"priority":{"type":"static_select","selected_option":{"value":"high"}}}
        }}}}""");

    var form = (SelectForm) binding.resolve(req, null);
    assertThat(form.priority()).isEqualTo("high");
  }

  public record SelectForm(String priority) {}

  public record SingleField(String value) {}

  @Test
  void handlesTimePicker() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"timepicker","selected_time":"14:30"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("14:30");
  }

  @Test
  void handlesUsersSelect() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"users_select","selected_user":"U999"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("U999");
  }

  @Test
  void handlesConversationsSelect() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"conversations_select","selected_conversation":"C999"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("C999");
  }

  @Test
  void handlesChannelsSelect() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"channels_select","selected_channel":"C888"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("C888");
  }

  @Test
  void handlesUrlTextInput() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"url_text_input","value":"https://example.com"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("https://example.com");
  }

  @Test
  void handlesEmailTextInput() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"email_text_input","value":"bob@example.com"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("bob@example.com");
  }

  @Test
  void handlesNumberInput() {
    var binding = new BlockParameterBinding("b", TypedForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"name":{"type":"plain_text_input","value":"Item"},"age":{"type":"number_input","value":"7"}}
        }}}}""");
    var form = (TypedForm) binding.resolve(req, null);
    assertThat(form.age()).isEqualTo(7);
  }

  @Test
  void handlesExternalSelect() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"external_select","selected_option":{"value":"ext-val"}}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("ext-val");
  }

  public record ListField(List<String> value) {}

  @Test
  void handlesMultiStaticSelect() {
    var binding = new BlockParameterBinding("b", ListField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"multi_static_select","selected_options":[{"value":"a"},{"value":"b"}]}}
        }}}}""");
    var form = (ListField) binding.resolve(req, null);
    assertThat(form.value()).containsExactly("a", "b");
  }

  @Test
  void handlesMultiExternalSelect() {
    var binding = new BlockParameterBinding("b", ListField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"multi_external_select","selected_options":[{"value":"x"},{"value":"y"}]}}
        }}}}""");
    var form = (ListField) binding.resolve(req, null);
    assertThat(form.value()).containsExactly("x", "y");
  }

  @Test
  void handlesMultiUsersSelect() {
    var binding = new BlockParameterBinding("b", ListField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"multi_users_select","selected_users":["U1","U2"]}}
        }}}}""");
    var form = (ListField) binding.resolve(req, null);
    assertThat(form.value()).containsExactly("U1", "U2");
  }

  @Test
  void handlesMultiConversationsSelect() {
    var binding = new BlockParameterBinding("b", ListField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"multi_conversations_select","selected_conversations":["C1","C2"]}}
        }}}}""");
    var form = (ListField) binding.resolve(req, null);
    assertThat(form.value()).containsExactly("C1", "C2");
  }

  @Test
  void handlesMultiChannelsSelect() {
    var binding = new BlockParameterBinding("b", ListField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"multi_channels_select","selected_channels":["C3","C4"]}}
        }}}}""");
    var form = (ListField) binding.resolve(req, null);
    assertThat(form.value()).containsExactly("C3", "C4");
  }

  public record RichTextField(RichTextBlock value) {}

  @Test
  void handlesRichTextInput() {
    var binding = new BlockParameterBinding("b", RichTextField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"rich_text_input","rich_text_value":{"type":"rich_text","block_id":"blk","elements":[]}}}
        }}}}""");
    var form = (RichTextField) binding.resolve(req, null);
    assertThat(form.value()).isNotNull();
  }

  public record FileField(List<File> value) {}

  @Test
  void handlesFileInput() {
    var binding = new BlockParameterBinding("b", FileField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"file_input","files":[{"id":"F1","name":"test.txt"}]}}
        }}}}""");
    var form = (FileField) binding.resolve(req, null);
    assertThat(form.value()).hasSize(1);
    assertThat(form.value().get(0).getId()).isEqualTo("F1");
  }

  // --- @ActionId tests ---

  public record ActionIdForm(@ActionId("custom-action") String title) {}

  @Test
  void bindsWithExplicitActionId() {
    var binding = new BlockParameterBinding("b", ActionIdForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"custom-action":{"type":"plain_text_input","value":"Explicit"}}
        }}}}""");
    var form = (ActionIdForm) binding.resolve(req, null);
    assertThat(form.title()).isEqualTo("Explicit");
  }

  @Test
  void throwsWhenExplicitActionIdNotFound() {
    var binding = new BlockParameterBinding("b", ActionIdForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"wrong-action":{"type":"plain_text_input","value":"x"}}
        }}}}""");
    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("custom-action");
  }

  // --- Name resolution coverage ---

  @Test
  void resolvesBySnakeCase() {
    // Record field "firstName" should match "first_name"
    var binding = new BlockParameterBinding("b", FirstNameForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"first_name":{"type":"plain_text_input","value":"Snake"}}
        }}}}""");
    var form = (FirstNameForm) binding.resolve(req, null);
    assertThat(form.firstName()).isEqualTo("Snake");
  }

  public record FirstNameForm(String firstName) {}

  @Test
  void resolvesByCaseInsensitive() {
    // Record field "title" should match "TITLE"
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"VALUE":{"type":"plain_text_input","value":"CaseMatch"}}
        }}}}""");
    var form = (SingleField) binding.resolve(req, null);
    assertThat(form.value()).isEqualTo("CaseMatch");
  }

  @Test
  void resolveNameReturnsEmptyWhenNoMatch() {
    // Directly test the static method
    var result = BlockParameterBinding.resolveName("myField", java.util.Set.of("unrelated"));
    assertThat(result).isEmpty();
  }

  @Test
  void handlesUnknownInputType() {
    var binding = new BlockParameterBinding("b", SingleField.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "b":{"value":{"type":"future_widget","value":"x"}}
        }}}}""");
    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("future_widget");
  }

  @Test
  void throwsWhenBlockNotFound() {
    var binding = new BlockParameterBinding("missing-block", SimpleForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "other-block":{"field":{"type":"plain_text_input","value":"x"}}
        }}}}""");

    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("missing-block");
  }

  @Test
  void throwsWhenFieldNotFound() {
    var binding = new BlockParameterBinding("my-block", SimpleForm.class, conversionService);
    var req =
        TestRequests.viewSubmission(
            """
        {"user":{"id":"U1","name":"bob"},"team":{"id":"T1"},"view":{"state":{"values":{
          "my-block":{"other-field":{"type":"plain_text_input","value":"x"}}
        }}}}""");

    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("title");
  }

  @Test
  void throwsForUnsupportedRequestType() {
    var binding = new BlockParameterBinding("block", SimpleForm.class, conversionService);
    var req = TestRequests.slashCommand("text=hello");

    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
