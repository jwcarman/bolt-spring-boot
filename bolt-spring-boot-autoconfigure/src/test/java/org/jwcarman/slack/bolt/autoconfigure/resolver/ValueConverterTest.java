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

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

class ValueConverterTest {

  private final DefaultConversionService conversionService = new DefaultConversionService();

  @Test
  void returnsValueWhenAlreadyCorrectType() {
    assertThat(ValueConverter.convert("hello", String.class, conversionService)).isEqualTo("hello");
  }

  @Test
  void convertsStringToInteger() {
    assertThat(ValueConverter.convert("42", Integer.class, conversionService)).isEqualTo(42);
  }

  @Test
  void convertsStringToLong() {
    assertThat(ValueConverter.convert("123456789", Long.class, conversionService))
        .isEqualTo(123456789L);
  }

  @Test
  void convertsStringToBoolean() {
    assertThat(ValueConverter.convert("true", Boolean.class, conversionService)).isEqualTo(true);
  }
}
