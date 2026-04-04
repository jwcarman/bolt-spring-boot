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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.TestRequests;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ChannelId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ResponseUrl;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TeamId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

@SuppressWarnings("unused")
class ParameterBindingsTest {

  // --- Test helper methods with annotated parameters ---

  // These methods exist only as reflection targets for parameter annotation tests.
  // They are never called directly — ParameterBindingsTest uses getDeclaredMethod()
  // to inspect their parameter annotations.

  void userIdMethod(@UserId String userId) {
    // Reflection target
  }

  void userNameMethod(@UserName String userName) {
    // Reflection target
  }

  void teamIdMethod(@TeamId String teamId) {
    // Reflection target
  }

  void channelIdMethod(@ChannelId String channelId) {
    // Reflection target
  }

  void triggerIdMethod(@TriggerId String triggerId) {
    // Reflection target
  }

  void responseUrlMethod(@ResponseUrl String responseUrl) {
    // Reflection target
  }

  void commandTextMethod(@CommandText String commandText) {
    // Reflection target
  }

  void actionValueMethod(@ActionValue String actionValue) {
    // Reflection target
  }

  void messageTextMethod(@MessageText String messageText) {
    // Reflection target
  }

  void requestTypeMethod(SlashCommandRequest request) {
    // Reflection target
  }

  void contextTypeMethod(SlashCommandContext context) {
    // Reflection target
  }

  void unresolvableMethod(String unknown) {
    // Reflection target
  }

  void noParamsMethod() {
    // Reflection target
  }

  void multiParamMethod(@UserId String userId, @TeamId String teamId) {
    // Reflection target
  }

  void primitiveIntMethod(@UserId int userId) {
    // Reflection target
  }

  // --- resolve(Parameter, requestType, contextType) tests via resolve(Method, ...) ---

  @Test
  void resolvesUserIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("user_id=U123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("U123");
  }

  @Test
  void resolvesUserNameAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("userNameMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("user_name=jsmith");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("jsmith");
  }

  @Test
  void resolvesTeamIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("teamIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("team_id=T123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("T123");
  }

  @Test
  void resolvesChannelIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("channelIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("channel_id=C123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("C123");
  }

  @Test
  void resolvesTriggerIdAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("triggerIdMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("trigger_id=tr123");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("tr123");
  }

  @Test
  void resolvesResponseUrlAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("responseUrlMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("response_url=https://hooks.slack.com/1");
    assertThat(bindings[0].resolve(req, null)).isEqualTo("https://hooks.slack.com/1");
  }

  @Test
  void resolvesCommandTextAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("commandTextMethod", String.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("text=hello");
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
    var req = TestRequests.slashCommand("user_id=U1");
    assertThat(bindings[0].resolve(req, null)).isSameAs(req);
  }

  @Test
  void resolvesContextType() throws Exception {
    Method method = getClass().getDeclaredMethod("contextTypeMethod", SlashCommandContext.class);
    ParameterBinding[] bindings =
        ParameterBindings.resolve(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var ctx = new SlashCommandContext();
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
    var req = TestRequests.slashCommand("user_id=U999&team_id=T999");
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
