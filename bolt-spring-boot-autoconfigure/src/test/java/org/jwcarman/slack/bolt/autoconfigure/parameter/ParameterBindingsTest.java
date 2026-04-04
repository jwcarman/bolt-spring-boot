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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ChannelId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ResponseUrl;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TeamId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;
import org.mockito.Answers;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

@SuppressWarnings("unused")
class ParameterBindingsTest {

  // --- Test helper methods with annotated parameters ---

  void userIdMethod(@UserId String userId) {}

  void userNameMethod(@UserName String userName) {}

  void teamIdMethod(@TeamId String teamId) {}

  void channelIdMethod(@ChannelId String channelId) {}

  void triggerIdMethod(@TriggerId String triggerId) {}

  void responseUrlMethod(@ResponseUrl String responseUrl) {}

  void commandTextMethod(@CommandText String commandText) {}

  void actionValueMethod(@ActionValue String actionValue) {}

  void messageTextMethod(@MessageText String messageText) {}

  void requestTypeMethod(SlashCommandRequest request) {}

  void contextTypeMethod(SlashCommandContext context) {}

  void unresolvableMethod(String unknown) {}

  void noParamsMethod() {}

  void multiParamMethod(@UserId String userId, @TeamId String teamId) {}

  void primitiveIntMethod(@UserId int userId) {}

  // --- resolve(Parameter, requestType, contextType) tests via resolve(Method, ...) ---

  @Test
  void resolvesUserIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUserId()).thenReturn("U123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("U123");
  }

  @Test
  void resolvesUserNameAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("userNameMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUserName()).thenReturn("jsmith");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("jsmith");
  }

  @Test
  void resolvesTeamIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("teamIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeamId()).thenReturn("T123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("T123");
  }

  @Test
  void resolvesChannelIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("channelIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannelId()).thenReturn("C123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("C123");
  }

  @Test
  void resolvesTriggerIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("triggerIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("tr123");
  }

  @Test
  void resolvesResponseUrlAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("responseUrlMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getResponseUrl()).thenReturn("https://hooks.slack.com/1");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("https://hooks.slack.com/1");
  }

  @Test
  void resolvesCommandTextAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("commandTextMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getText()).thenReturn("hello");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("hello");
  }

  @Test
  void resolvesActionValueAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("actionValueMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
  }

  @Test
  void resolvesMessageTextAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("messageTextMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
  }

  @Test
  void resolvesRequestType() throws Exception {
    Method method = getClass().getDeclaredMethod("requestTypeMethod", SlashCommandRequest.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = mock(SlashCommandRequest.class);
    assertThat(bindings[0].resolve(req, null)).isSameAs(req);
  }

  @Test
  void resolvesContextType() throws Exception {
    Method method = getClass().getDeclaredMethod("contextTypeMethod", SlashCommandContext.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var ctx = mock(SlashCommandContext.class);
    assertThat(bindings[0].resolve(null, ctx)).isSameAs(ctx);
  }

  @Test
  void throwsForUnresolvableParameter() throws Exception {
    Method method = getClass().getDeclaredMethod("unresolvableMethod", String.class);
    assertThatThrownBy(
            () ->
                ParameterBindings.resolve(
                    method, SlashCommandRequest.class, SlashCommandContext.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void returnsEmptyBindingsForNoParams() throws Exception {
    Method method = getClass().getDeclaredMethod("noParamsMethod");
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).isSameAs(ParameterBindings.EMPTY_BINDINGS);
  }

  // --- resolve(ParameterBinding[], request, context) tests ---

  @Test
  void resolveEmptyArrayReturnsEmptyArray() {
    Object[] result = ParameterBindings.resolve(ParameterBindings.EMPTY_BINDINGS, null, null);
    assertThat(result).isSameAs(ParameterBindings.EMPTY_PARAMETERS);
  }

  @Test
  void resolveBindingsInOrder() {
    ParameterBinding first = (req, ctx) -> "first";
    ParameterBinding second = (req, ctx) -> "second";
    Object[] result = ParameterBindings.resolve(new ParameterBinding[] {first, second}, null, null);
    assertThat(result).containsExactly("first", "second");
  }

  @Test
  void resolveMultipleAnnotatedParams() throws Exception {
    Method method = getClass().getDeclaredMethod("multiParamMethod", String.class, String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(2);
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUserId()).thenReturn("U999");
    when(req.getPayload().getTeamId()).thenReturn("T999");
    Object[] result = ParameterBindings.resolve(bindings, req, null);
    assertThat(result).containsExactly("U999", "T999");
  }

  // --- nullSafe wrapper tests ---

  @Test
  void nullSafeReturnsNullForReferenceTypeWhenNull() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> null;
    ParameterBinding wrapped = ParameterBindings.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isNull();
  }

  @Test
  void nullSafeReturnsZeroForPrimitiveIntWhenNull() throws Exception {
    Method method = getClass().getDeclaredMethod("primitiveIntMethod", int.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> null;
    ParameterBinding wrapped = ParameterBindings.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isEqualTo(0);
  }

  @Test
  void nullSafePassesThroughNonNullValue() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> "U123";
    ParameterBinding wrapped = ParameterBindings.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isEqualTo("U123");
  }
}
