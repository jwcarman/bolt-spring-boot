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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionId;
import org.jwcarman.slack.bolt.autoconfigure.reflect.Types;
import org.springframework.core.convert.ConversionService;

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.model.view.ViewState;

/**
 * Binds a view submission block's fields to a Java record. Uses convention-based name matching
 * (exact, kebab-case, snake_case, case-insensitive) and Spring's {@link ConversionService} for type
 * coercion.
 *
 * <p>Record component metadata and type coercion are precomputed at startup. Only name resolution
 * and value extraction happen at request time.
 */
public final class BlockParameterBinding implements ParameterBinding {

  private final String blockName;
  private final Class<?> recordType;
  private final List<FieldBinding> fieldBindings;

  /**
   * Creates a new block parameter binding. Precomputes field metadata from the record type.
   *
   * @param blockName the block name to look up in the view state
   * @param recordType the record class to bind to
   * @param conversionService the conversion service for type coercion
   */
  public BlockParameterBinding(
      String blockName, Class<?> recordType, ConversionService conversionService) {
    this.blockName = blockName;
    this.recordType = recordType;
    this.fieldBindings = buildFieldBindings(recordType, conversionService);
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
    Object[] args = new Object[fieldBindings.size()];

    for (int i = 0; i < fieldBindings.size(); i++) {
      args[i] = fieldBindings.get(i).resolve(blockValues, resolvedBlock);
    }

    return Types.newRecord(recordType, args);
  }

  private static List<FieldBinding> buildFieldBindings(
      Class<?> recordType, ConversionService conversionService) {
    RecordComponent[] components = recordType.getRecordComponents();
    return java.util.Arrays.stream(components)
        .map(component -> FieldBinding.of(component, conversionService))
        .toList();
  }

  /**
   * Precomputed binding for a single record field. If {@code explicitActionId} is set (from {@link
   * ActionId}), uses it directly. Otherwise resolves the field name via convention matching.
   */
  private record FieldBinding(
      String fieldName,
      String explicitActionId,
      Class<?> targetType,
      ConversionService conversionService) {

    static FieldBinding of(RecordComponent component, ConversionService conversionService) {
      ActionId actionId = component.getAnnotation(ActionId.class);
      String explicit = actionId != null ? actionId.value() : null;
      return new FieldBinding(
          component.getName(), explicit, component.getType(), conversionService);
    }

    Object resolve(Map<String, ViewState.Value> blockValues, String blockName) {
      String fieldKey = resolveFieldKey(blockValues.keySet(), blockName);
      ViewState.Value value = blockValues.get(fieldKey);
      Object rawValue = extractValue(value);
      return convert(rawValue);
    }

    private String resolveFieldKey(Set<String> candidates, String blockName) {
      if (explicitActionId != null) {
        if (!candidates.contains(explicitActionId)) {
          throw new IllegalArgumentException(
              "Action ID '" + explicitActionId + "' not found in block '" + blockName + "'");
        }
        return explicitActionId;
      }
      return resolveName(fieldName, candidates)
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      "Field '" + fieldName + "' not found in block '" + blockName + "'"));
    }

    private Object convert(Object rawValue) {
      if (targetType.isInstance(rawValue)) {
        return rawValue;
      }
      return conversionService.convert(rawValue, targetType);
    }
  }

  static Object extractValue(ViewState.Value value) {
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

  static Optional<String> resolveName(String javaName, Set<String> candidates) {
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
