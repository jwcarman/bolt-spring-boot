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

import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.convert.ConversionService;

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.model.view.ViewState;

/**
 * Binds a view submission block's fields to a Java record. Uses convention-based name matching
 * (exact, kebab-case, snake_case, case-insensitive) and Spring's {@link ConversionService} for type
 * coercion.
 */
public final class BlockParameterBinding implements ParameterBinding {

  private final String blockName;
  private final Class<?> recordType;
  private final ConversionService conversionService;

  /**
   * Creates a new block parameter binding.
   *
   * @param blockName the block name to look up in the view state
   * @param recordType the record class to bind to
   * @param conversionService the conversion service for type coercion
   */
  public BlockParameterBinding(
      String blockName, Class<?> recordType, ConversionService conversionService) {
    this.blockName = blockName;
    this.recordType = recordType;
    this.conversionService = conversionService;
  }

  @Override
  public Object resolve(Object request, Object context) {
    if (!(request instanceof ViewSubmissionRequest viewReq)) {
      throw new IllegalArgumentException(
          "@Block not supported for " + request.getClass().getSimpleName());
    }

    Map<String, Map<String, ViewState.Value>> stateValues =
        viewReq.getPayload().getView().getState().getValues();

    String resolvedBlock =
        resolveName(blockName, stateValues.keySet())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Block '" + blockName + "' not found in view state"));

    Map<String, ViewState.Value> blockValues = stateValues.get(resolvedBlock);
    RecordComponent[] components = recordType.getRecordComponents();
    Object[] args = new Object[components.length];

    for (int i = 0; i < components.length; i++) {
      RecordComponent component = components[i];
      String fieldKey =
          resolveName(component.getName(), blockValues.keySet())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Field '"
                              + component.getName()
                              + "' not found in block '"
                              + resolvedBlock
                              + "'"));

      ViewState.Value value = blockValues.get(fieldKey);
      Object rawValue = extractValue(value);
      args[i] = convert(rawValue, component.getType());
    }

    try {
      Class<?>[] paramTypes = new Class[components.length];
      for (int i = 0; i < components.length; i++) {
        paramTypes[i] = components[i].getType();
      }
      return recordType.getDeclaredConstructor(paramTypes).newInstance(args);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Failed to construct " + recordType.getSimpleName(), e);
    }
  }

  private Object convert(Object rawValue, Class<?> targetType) {
    if (targetType.isInstance(rawValue)) {
      return rawValue;
    }
    return conversionService.convert(rawValue, targetType);
  }

  private static Object extractValue(ViewState.Value value) {
    return switch (value.getType()) {
      case "plain_text_input", "url_text_input", "email_text_input", "number_input" ->
          value.getValue();
      case "datepicker" -> value.getSelectedDate();
      case "timepicker" -> value.getSelectedTime();
      case "static_select", "external_select" -> value.getSelectedOption().getValue();
      case "multi_static_select", "multi_external_select" ->
          value.getSelectedOptions().stream().map(ViewState.SelectedOption::getValue).toList();
      case "users_select" -> value.getSelectedUser();
      case "multi_users_select" -> value.getSelectedUsers();
      case "conversations_select" -> value.getSelectedConversation();
      case "multi_conversations_select" -> value.getSelectedConversations();
      case "channels_select" -> value.getSelectedChannel();
      case "multi_channels_select" -> value.getSelectedChannels();
      case "rich_text_input" -> value.getRichTextValue();
      case "file_input" -> value.getFiles();
      default -> throw new IllegalArgumentException("Unknown input type: " + value.getType());
    };
  }

  private static Optional<String> resolveName(String javaName, Set<String> candidates) {
    if (candidates.contains(javaName)) {
      return Optional.of(javaName);
    }
    String kebab = toKebabCase(javaName);
    if (candidates.contains(kebab)) {
      return Optional.of(kebab);
    }
    String snake = toSnakeCase(javaName);
    if (candidates.contains(snake)) {
      return Optional.of(snake);
    }
    String lower = javaName.toLowerCase();
    for (String candidate : candidates) {
      if (candidate.toLowerCase().equals(lower)) {
        return Optional.of(candidate);
      }
    }
    return Optional.empty();
  }

  private static String toKebabCase(String javaName) {
    return javaName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
  }

  private static String toSnakeCase(String javaName) {
    return javaName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
  }
}
