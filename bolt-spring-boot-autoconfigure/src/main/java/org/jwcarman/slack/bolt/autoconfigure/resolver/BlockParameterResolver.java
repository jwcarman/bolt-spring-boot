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

import java.lang.reflect.RecordComponent;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.model.view.ViewState;

/**
 * Resolves a Java record parameter by binding its components to fields from the view submission
 * state. This resolver uses {@link NameResolver} for fuzzy name matching (supporting camelCase to
 * kebab-case conversion), {@link ValueExtractor} to extract raw values from {@link
 * ViewState.Value}, and {@link ValueConverter} to coerce values to the target component types.
 */
public class BlockParameterResolver {

  /**
   * Creates a {@link ParameterResolver} that binds a view submission block's fields to a Java
   * record.
   *
   * @param blockName the block name (camelCase or kebab-case) to look up in the view state
   * @param recordType the record class whose components will be populated from the block's fields
   * @param conversionService the conversion service used to coerce raw string values to component
   *     types
   * @return a parameter resolver that constructs the record from the view submission state
   */
  public static ParameterResolver create(
      String blockName, Class<?> recordType, ConversionService conversionService) {
    return (req, ctx) -> {
      ViewSubmissionRequest viewReq = (ViewSubmissionRequest) req;
      Map<String, Map<String, ViewState.Value>> stateValues =
          viewReq.getPayload().getView().getState().getValues();

      String resolvedBlock =
          NameResolver.resolve(blockName, stateValues.keySet())
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
            NameResolver.resolve(component.getName(), blockValues.keySet())
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Field '"
                                + component.getName()
                                + "' not found in block '"
                                + resolvedBlock
                                + "'"));

        ViewState.Value value = blockValues.get(fieldKey);
        Object rawValue = ValueExtractor.extract(value);
        args[i] = ValueConverter.convert(rawValue, component.getType(), conversionService);
      }

      try {
        Class<?>[] paramTypes = new Class[components.length];
        for (int i = 0; i < components.length; i++) {
          paramTypes[i] = components[i].getType();
        }
        return recordType.getDeclaredConstructor(paramTypes).newInstance(args);
      } catch (Exception e) {
        throw new RuntimeException("Failed to construct " + recordType.getSimpleName(), e);
      }
    };
  }
}
