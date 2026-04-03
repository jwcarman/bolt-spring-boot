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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import com.slack.api.app_backend.views.payload.ViewSubmissionPayload;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;

class BlockParameterResolverTest {

  private final DefaultConversionService conversionService = new DefaultConversionService();

  public record SimpleForm(String title, String description) {}

  @Test
  void bindsRecordFromViewState() {
    ViewState.Value titleValue = new ViewState.Value();
    titleValue.setType("plain_text_input");
    titleValue.setValue("My Title");

    ViewState.Value descValue = new ViewState.Value();
    descValue.setType("plain_text_input");
    descValue.setValue("My Description");

    ViewSubmissionRequest req =
        buildRequest(
            Map.of(
                "simple-form",
                Map.of(
                    "title", titleValue,
                    "description", descValue)));

    ParameterResolver resolver =
        BlockParameterResolver.create("simpleForm", SimpleForm.class, conversionService);

    SimpleForm form = (SimpleForm) resolver.resolve(req, null);
    assertThat(form.title()).isEqualTo("My Title");
    assertThat(form.description()).isEqualTo("My Description");
  }

  @Test
  void bindsWithExplicitBlockName() {
    ViewState.Value titleValue = new ViewState.Value();
    titleValue.setType("plain_text_input");
    titleValue.setValue("Explicit");

    ViewSubmissionRequest req =
        buildRequest(Map.of("my-custom-block", Map.of("title", titleValue)));

    // Use a record with just "title" since we only have one field
    ParameterResolver resolver =
        BlockParameterResolver.create("my-custom-block", TitleOnly.class, conversionService);

    TitleOnly form = (TitleOnly) resolver.resolve(req, null);
    assertThat(form.title()).isEqualTo("Explicit");
  }

  public record TitleOnly(String title) {}

  public record TypedForm(String name, Integer age) {}

  @Test
  void convertsTypesViaConversionService() {
    ViewState.Value nameValue = new ViewState.Value();
    nameValue.setType("plain_text_input");
    nameValue.setValue("James");

    ViewState.Value ageValue = new ViewState.Value();
    ageValue.setType("plain_text_input");
    ageValue.setValue("42");

    ViewSubmissionRequest req =
        buildRequest(Map.of("typed-form", Map.of("name", nameValue, "age", ageValue)));

    ParameterResolver resolver =
        BlockParameterResolver.create("typedForm", TypedForm.class, conversionService);

    TypedForm form = (TypedForm) resolver.resolve(req, null);
    assertThat(form.name()).isEqualTo("James");
    assertThat(form.age()).isEqualTo(42);
  }

  @Test
  void handlesKebabCaseFieldNames() {
    ViewState.Value value = new ViewState.Value();
    value.setType("plain_text_input");
    value.setValue("test");

    ViewSubmissionRequest req = buildRequest(Map.of("my-block", Map.of("first-name", value)));

    ParameterResolver resolver =
        BlockParameterResolver.create("myBlock", FirstNameForm.class, conversionService);

    FirstNameForm form = (FirstNameForm) resolver.resolve(req, null);
    assertThat(form.firstName()).isEqualTo("test");
  }

  public record FirstNameForm(String firstName) {}

  private ViewSubmissionRequest buildRequest(
      Map<String, Map<String, ViewState.Value>> stateValues) {
    ViewState state = new ViewState();
    state.setValues(stateValues);
    View view = new View();
    view.setState(state);
    ViewSubmissionPayload payload = mock(ViewSubmissionPayload.class);
    when(payload.getView()).thenReturn(view);
    ViewSubmissionRequest req = mock(ViewSubmissionRequest.class);
    when(req.getPayload()).thenReturn(payload);
    return req;
  }
}
