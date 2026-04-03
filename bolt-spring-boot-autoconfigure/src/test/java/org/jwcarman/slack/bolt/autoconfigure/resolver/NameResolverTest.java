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

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

class NameResolverTest {

  @Test
  void resolvesExactMatch() {
    Set<String> candidates = Set.of("firstName", "last-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("firstName"));
  }

  @Test
  void resolvesKebabCase() {
    Set<String> candidates = Set.of("first-name", "last-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first-name"));
  }

  @Test
  void resolvesSnakeCase() {
    Set<String> candidates = Set.of("first_name", "last_name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first_name"));
  }

  @Test
  void resolvesCaseInsensitive() {
    Set<String> candidates = Set.of("FIRSTNAME", "LASTNAME");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("FIRSTNAME"));
  }

  @Test
  void returnsEmptyWhenNoMatch() {
    Set<String> candidates = Set.of("unrelated", "other");
    assertThat(NameResolver.resolve("firstName", candidates)).isEmpty();
  }

  @Test
  void prefersExactOverKebab() {
    Set<String> candidates = Set.of("firstName", "first-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("firstName"));
  }

  @Test
  void prefersKebabOverSnake() {
    Set<String> candidates = Set.of("first-name", "first_name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first-name"));
  }

  @Test
  void handlesSingleWordName() {
    Set<String> candidates = Set.of("title");
    assertThat(NameResolver.resolve("title", candidates)).isEqualTo(Optional.of("title"));
  }
}
