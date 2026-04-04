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

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.TestRequests;

class ActionValueParameterBindingTest {

  private final ParameterBinding binding = new ActionValueParameterBinding();

  @Test
  void extractsFromBlockAction() {
    var req =
        TestRequests.blockAction(
            """
        {"user":{"id":"U1"},"team":{"id":"T1"},\
        "actions":[{"action_id":"btn","value":"clicked_value"}]}""");
    assertThat(binding.resolve(req, null)).isEqualTo("clicked_value");
  }

  @Test
  void throwsForUnsupportedRequest() {
    var req = TestRequests.slashCommand("text=hello");
    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
