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

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;

@SuppressWarnings("unused")
class MethodBindingsTest {

  // --- Public static test bean so reflection can access across packages ---

  public static class TestBean {

    public Response responseMethod() {
      return Response.ok();
    }

    public String stringMethod() {
      return "hello";
    }

    public void voidMethod() {}

    public Map<String, String> jsonMethod() {
      return Map.of("key", "value");
    }
  }

  private final TestBean bean = new TestBean();

  @Test
  void createsResponseMethodBinding() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("responseMethod");
    MethodBinding<SlashCommandRequest, SlashCommandContext> binding =
        MethodBindings.create(bean, method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(binding).isInstanceOf(ResponseMethodBinding.class);
  }

  @Test
  void createsStringMethodBinding() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("stringMethod");
    MethodBinding<SlashCommandRequest, SlashCommandContext> binding =
        MethodBindings.create(bean, method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(binding).isInstanceOf(StringMethodBinding.class);
  }

  @Test
  void createsVoidMethodBinding() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("voidMethod");
    MethodBinding<SlashCommandRequest, SlashCommandContext> binding =
        MethodBindings.create(bean, method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(binding).isInstanceOf(VoidMethodBinding.class);
  }

  @Test
  void createsJsonMethodBindingForOtherReturnTypes() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("jsonMethod");
    MethodBinding<SlashCommandRequest, SlashCommandContext> binding =
        MethodBindings.create(bean, method, SlashCommandRequest.class, SlashCommandContext.class);
    assertThat(binding).isInstanceOf(JsonMethodBinding.class);
  }
}
