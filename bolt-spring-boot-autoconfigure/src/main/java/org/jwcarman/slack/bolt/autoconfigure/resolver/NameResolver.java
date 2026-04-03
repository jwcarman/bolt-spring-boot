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

import java.util.Optional;
import java.util.Set;

public class NameResolver {

  public static Optional<String> resolve(String javaName, Set<String> candidates) {
    // 1. Exact match
    if (candidates.contains(javaName)) {
      return Optional.of(javaName);
    }

    // 2. Kebab-case
    String kebab = toKebabCase(javaName);
    if (candidates.contains(kebab)) {
      return Optional.of(kebab);
    }

    // 3. Snake_case
    String snake = toSnakeCase(javaName);
    if (candidates.contains(snake)) {
      return Optional.of(snake);
    }

    // 4. Case-insensitive
    String lowerName = javaName.toLowerCase();
    for (String candidate : candidates) {
      if (candidate.toLowerCase().equals(lowerName)) {
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
