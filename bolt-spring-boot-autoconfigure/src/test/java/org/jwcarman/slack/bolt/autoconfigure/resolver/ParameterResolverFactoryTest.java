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
import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
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
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

@SuppressWarnings("unused")
class ParameterResolverFactoryTest {

  private final DefaultConversionService conversionService = new DefaultConversionService();

  // --- Sample methods with annotated parameters for reflection ---

  void userIdMethod(@UserId String userId) {}

  void userNameMethod(@UserName String userName) {}

  void teamIdMethod(@TeamId String teamId) {}

  void channelIdMethod(@ChannelId String channelId) {}

  void triggerIdMethod(@TriggerId String triggerId) {}

  void responseUrlMethod(@ResponseUrl String responseUrl) {}

  void commandTextMethod(@CommandText String text) {}

  void actionValueMethod(@ActionValue String value) {}

  void messageTextMethod(@MessageText String text) {}

  void blockMethod(@Block("my-block") TestRecord block) {}

  void requestTypeMethod(SlashCommandRequest req) {}

  void contextTypeMethod(SlashCommandContext ctx) {}

  void unresolvableMethod(String unknown) {}

  record TestRecord(String field) {}

  // --- Tests ---

  @Test
  void shouldResolveUserIdAnnotation() throws Exception {
    Parameter param = getParameter("userIdMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveUserNameAnnotation() throws Exception {
    Parameter param = getParameter("userNameMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveTeamIdAnnotation() throws Exception {
    Parameter param = getParameter("teamIdMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveChannelIdAnnotation() throws Exception {
    Parameter param = getParameter("channelIdMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveTriggerIdAnnotation() throws Exception {
    Parameter param = getParameter("triggerIdMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveResponseUrlAnnotation() throws Exception {
    Parameter param = getParameter("responseUrlMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveCommandTextAnnotation() throws Exception {
    Parameter param = getParameter("commandTextMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveActionValueAnnotation() throws Exception {
    Parameter param = getParameter("actionValueMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveMessageTextAnnotation() throws Exception {
    Parameter param = getParameter("messageTextMethod", String.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveBlockAnnotation() throws Exception {
    Parameter param = getParameter("blockMethod", TestRecord.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);
    assertThat(resolver).isNotNull();
  }

  @Test
  void shouldResolveRequestType() throws Exception {
    Parameter param = getParameter("requestTypeMethod", SlashCommandRequest.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);

    Object mockReq = mock(SlashCommandRequest.class);
    Object mockCtx = mock(SlashCommandContext.class);
    assertThat(resolver.resolve(mockReq, mockCtx)).isSameAs(mockReq);
  }

  @Test
  void shouldResolveContextType() throws Exception {
    Parameter param = getParameter("contextTypeMethod", SlashCommandContext.class);
    ParameterResolver resolver =
        ParameterResolverFactory.createResolver(
            param, SlashCommandRequest.class, SlashCommandContext.class, conversionService);

    Object mockReq = mock(SlashCommandRequest.class);
    Object mockCtx = mock(SlashCommandContext.class);
    assertThat(resolver.resolve(mockReq, mockCtx)).isSameAs(mockCtx);
  }

  @Test
  void shouldThrowForUnresolvableParameter() throws Exception {
    Parameter param = getParameter("unresolvableMethod", String.class);
    assertThatThrownBy(
            () ->
                ParameterResolverFactory.createResolver(
                    param, SlashCommandRequest.class, SlashCommandContext.class, conversionService))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot resolve parameter");
  }

  private Parameter getParameter(String methodName, Class<?>... paramTypes) throws Exception {
    return this.getClass().getDeclaredMethod(methodName, paramTypes).getParameters()[0];
  }
}
