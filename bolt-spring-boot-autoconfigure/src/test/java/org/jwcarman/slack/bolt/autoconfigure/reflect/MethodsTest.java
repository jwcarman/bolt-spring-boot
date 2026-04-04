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
package org.jwcarman.slack.bolt.autoconfigure.reflect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.SlackHandlerInvocationException;

class MethodsTest {

  // --- Public static test bean so reflection can access it ---

  public static class TestBean {

    public String hello() {
      return "world";
    }

    private String privateMethod() {
      return "secret";
    }
  }

  private final TestBean bean = new TestBean();

  @Test
  void invokesPublicMethod() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("hello");
    String result = Methods.invoke(String.class, bean, method, new Object[0]);
    assertThat(result).isEqualTo("world");
  }

  @Test
  void throwsSlackHandlerInvocationExceptionOnError() throws Exception {
    Method method = TestBean.class.getDeclaredMethod("privateMethod");
    assertThatThrownBy(() -> Methods.invoke(String.class, bean, method, new Object[0]))
        .isInstanceOf(SlackHandlerInvocationException.class);
  }
}
