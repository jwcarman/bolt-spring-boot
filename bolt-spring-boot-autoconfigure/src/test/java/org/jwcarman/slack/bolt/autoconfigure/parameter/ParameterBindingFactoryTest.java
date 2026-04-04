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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.jwcarman.slack.bolt.autoconfigure.TestRequests;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.Block;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ChannelId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ResponseUrl;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TeamId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;
import org.springframework.core.convert.support.DefaultConversionService;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

@SuppressWarnings("unused")
class ParameterBindingFactoryTest {

  private final ParameterBindingFactory factory =
      new ParameterBindingFactory(DefaultConversionService.getSharedInstance());

  // --- Test helper methods with annotated parameters ---

  // These methods exist only as reflection targets for parameter annotation tests.
  // They are never called directly — ParameterBindingFactoryTest uses getDeclaredMethod()
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

  public record SimpleForm(String title) {}

  void blockWithExplicitName(@Block("my-block") SimpleForm form) {
    // Reflection target
  }

  void blockWithDefaultName(@Block SimpleForm form) {
    // Reflection target
  }

  // --- createBindings(Method, requestType, contextType) tests ---

  static Stream<Arguments> annotationBindings() {
    return Stream.of(
        Arguments.of("userIdMethod", "user_id=U123", "U123"),
        Arguments.of("userNameMethod", "user_name=jsmith", "jsmith"),
        Arguments.of("teamIdMethod", "team_id=T123", "T123"),
        Arguments.of("channelIdMethod", "channel_id=C123", "C123"),
        Arguments.of("triggerIdMethod", "trigger_id=tr123", "tr123"),
        Arguments.of(
            "responseUrlMethod",
            "response_url=https://hooks.slack.com/1",
            "https://hooks.slack.com/1"),
        Arguments.of("commandTextMethod", "text=hello", "hello"));
  }

  @ParameterizedTest
  @MethodSource("annotationBindings")
  void resolvesAnnotationBinding(String methodName, String formData, String expected)
      throws Exception {
    Method method = getClass().getDeclaredMethod(methodName, String.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand(formData);
    assertThat(bindings[0].resolve(req, null)).isEqualTo(expected);
  }

  @Test
  void resolvesActionValueAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("actionValueMethod", String.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
  }

  @Test
  void resolvesMessageTextAnnotation() throws Exception {
    Method method = getClass().getDeclaredMethod("messageTextMethod", String.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
  }

  @Test
  void resolvesBlockWithExplicitName() throws Exception {
    Method method = getClass().getDeclaredMethod("blockWithExplicitName", SimpleForm.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, ViewSubmissionRequest.class, ViewSubmissionContext.class);
    assertThat(bindings).hasSize(1);
    assertThat(bindings[0]).isNotNull();
  }

  @Test
  void resolvesBlockWithDefaultName() throws Exception {
    Method method = getClass().getDeclaredMethod("blockWithDefaultName", SimpleForm.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, ViewSubmissionRequest.class, ViewSubmissionContext.class);
    assertThat(bindings).hasSize(1);
    assertThat(bindings[0]).isNotNull();
  }

  @Test
  void resolvesRequestType() throws Exception {
    Method method = getClass().getDeclaredMethod("requestTypeMethod", SlashCommandRequest.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var req = TestRequests.slashCommand("user_id=U1");
    assertThat(bindings[0].resolve(req, null)).isSameAs(req);
  }

  @Test
  void resolvesContextType() throws Exception {
    Method method = getClass().getDeclaredMethod("contextTypeMethod", SlashCommandContext.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(1);
    var ctx = new SlashCommandContext();
    assertThat(bindings[0].resolve(null, ctx)).isSameAs(ctx);
  }

  @Test
  void throwsForUnresolvableParameter() throws Exception {
    Method method = getClass().getDeclaredMethod("unresolvableMethod", String.class);
    assertThatThrownBy(
            () ->
                factory.createBindings(
                    method, SlashCommandRequest.class, SlashCommandContext.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void returnsEmptyBindingsForNoParams() throws Exception {
    Method method = getClass().getDeclaredMethod("noParamsMethod");
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).isSameAs(ParameterBindingFactory.EMPTY_BINDINGS);
  }

  // --- resolve(ParameterBinding[], request, context) tests ---

  @Test
  void resolveEmptyArrayReturnsEmptyArray() {
    Object[] result =
        ParameterBindingFactory.resolve(ParameterBindingFactory.EMPTY_BINDINGS, null, null);
    assertThat(result).isSameAs(ParameterBindingFactory.EMPTY_PARAMETERS);
  }

  @Test
  void resolveBindingsInOrder() {
    ParameterBinding first = (req, ctx) -> "first";
    ParameterBinding second = (req, ctx) -> "second";
    Object[] result =
        ParameterBindingFactory.resolve(new ParameterBinding[] {first, second}, null, null);
    assertThat(result).containsExactly("first", "second");
  }

  @Test
  void resolveMultipleAnnotatedParams() throws Exception {
    Method method = getClass().getDeclaredMethod("multiParamMethod", String.class, String.class);
    ParameterBinding[] bindings =
        factory.createBindings(method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(bindings).hasSize(2);
    var req = TestRequests.slashCommand("user_id=U999&team_id=T999");
    Object[] result = ParameterBindingFactory.resolve(bindings, req, null);
    assertThat(result).containsExactly("U999", "T999");
  }

  // --- nullSafe wrapper tests ---

  @Test
  void nullSafeReturnsNullForReferenceTypeWhenNull() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> null;
    ParameterBinding wrapped = ParameterBindingFactory.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isNull();
  }

  @Test
  void nullSafeReturnsZeroForPrimitiveIntWhenNull() throws Exception {
    Method method = getClass().getDeclaredMethod("primitiveIntMethod", int.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> null;
    ParameterBinding wrapped = ParameterBindingFactory.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isEqualTo(0);
  }

  @Test
  void nullSafePassesThroughNonNullValue() throws Exception {
    Method method = getClass().getDeclaredMethod("userIdMethod", String.class);
    Parameter parameter = method.getParameters()[0];
    ParameterBinding original = (req, ctx) -> "U123";
    ParameterBinding wrapped = ParameterBindingFactory.nullSafe(parameter, original);
    assertThat(wrapped.resolve(null, null)).isEqualTo("U123");
  }
}
