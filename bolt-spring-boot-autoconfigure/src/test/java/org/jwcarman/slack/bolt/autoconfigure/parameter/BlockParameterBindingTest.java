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
import org.springframework.core.convert.support.DefaultConversionService;

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
