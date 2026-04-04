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
package org.jwcarman.slack.bolt.autoconfigure.method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;
import org.mockito.Answers;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;

@SuppressWarnings("unused")
class AbstractMethodBindingTest {

  // --- Test helper bean as a public static class so reflection can access it ---

  public static class TestBean {

    public Response responseHandler() {
      return Response.ok();
    }

    public String stringHandler() {
      return "hello";
    }

    public void voidHandler() {}

    public String wrongReturnType() {
      return "wrong";
    }
  }

  private final TestBean bean = new TestBean();

  @Test
  void rejectsMethodWithWrongReturnType() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("wrongReturnType");
    assertThatThrownBy(
            () ->
                new ResponseMethodBinding<SlashCommandRequest, SlashCommandContext>(
                    bean, method, new ParameterBinding[0]))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void responseMethodBindingPassesThroughResponse() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("responseHandler");
    var binding =
        new ResponseMethodBinding<SlashCommandRequest, SlashCommandContext>(
            bean, method, new ParameterBinding[0]);
    var req = mock(SlashCommandRequest.class);
    var ctx = mock(SlashCommandContext.class);
    Response result = binding.invoke(req, ctx);
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(200);
  }

  @Test
  void stringMethodBindingReturnsOkResponse() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("stringHandler");
    var binding =
        new StringMethodBinding<SlashCommandRequest, SlashCommandContext>(
            bean, method, new ParameterBinding[0]);
    var req = mock(SlashCommandRequest.class);
    var ctx = mock(SlashCommandContext.class);
    Response result = binding.invoke(req, ctx);
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(200);
    assertThat(result.getBody()).isEqualTo("hello");
  }

  @Test
  void voidMethodBindingCallsCtxAck() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("voidHandler");
    var binding =
        new VoidMethodBinding<SlashCommandRequest, SlashCommandContext>(
            bean, method, new ParameterBinding[0]);
    var req = mock(SlashCommandRequest.class);
    var ctx = mock(SlashCommandContext.class, Answers.RETURNS_DEEP_STUBS);
    var ackResponse = Response.ok();
    when(ctx.ack()).thenReturn(ackResponse);
    Response result = binding.invoke(req, ctx);
    assertThat(result).isSameAs(ackResponse);
  }

  @Test
  void jsonMethodBindingSerializesToJson() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("stringHandler");
    var binding =
        new JsonMethodBinding<SlashCommandRequest, SlashCommandContext>(
            bean, method, new ParameterBinding[0]);
    var req = mock(SlashCommandRequest.class);
    var ctx = mock(SlashCommandContext.class);
    var jsonElement = new com.google.gson.JsonPrimitive("hello");
    var jsonResponse = Response.ok("json");
    when(ctx.toJson("hello")).thenReturn(jsonElement);
    when(ctx.ack(jsonElement)).thenReturn(jsonResponse);
    Response result = binding.invoke(req, ctx);
    assertThat(result).isSameAs(jsonResponse);
  }
}
