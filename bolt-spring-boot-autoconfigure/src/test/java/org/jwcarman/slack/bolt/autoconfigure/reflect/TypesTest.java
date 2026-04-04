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

import org.junit.jupiter.api.Test;

class TypesTest {

  public record ValidRecord(String name, int age) {}

  @Test
  void newRecordConstructsSuccessfully() {
    var result = Types.newRecord(ValidRecord.class, new Object[] {"Alice", 30});
    assertThat(result).isInstanceOf(ValidRecord.class);
    var record = (ValidRecord) result;
    assertThat(record.name()).isEqualTo("Alice");
    assertThat(record.age()).isEqualTo(30);
  }

  public record ThrowingRecord(String value) {
    public ThrowingRecord {
      throw new RuntimeException("constructor failed");
    }
  }

  @Test
  void newRecordThrowsWhenConstructorFails() {
    assertThatThrownBy(() -> Types.newRecord(ThrowingRecord.class, new Object[] {"test"}))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ThrowingRecord");
  }

  @Test
  void returnsZeroForInt() {
    assertThat(Types.nullValue(int.class)).isEqualTo(0);
  }

  @Test
  void returnsZeroForLong() {
    assertThat(Types.nullValue(long.class)).isEqualTo(0L);
  }

  @Test
  void returnsZeroForShort() {
    assertThat(Types.nullValue(short.class)).isEqualTo((short) 0);
  }

  @Test
  void returnsZeroForByte() {
    assertThat(Types.nullValue(byte.class)).isEqualTo((byte) 0);
  }

  @Test
  void returnsZeroForDouble() {
    assertThat(Types.nullValue(double.class)).isEqualTo(0.0);
  }

  @Test
  void returnsZeroForFloat() {
    assertThat(Types.nullValue(float.class)).isEqualTo(0.0f);
  }

  @Test
  void returnsNullCharForChar() {
    assertThat(Types.nullValue(char.class)).isEqualTo('\0');
  }

  @Test
  void returnsFalseForBoolean() {
    assertThat(Types.nullValue(boolean.class)).isEqualTo(false);
  }

  @Test
  void returnsNullForReferenceType() {
    assertThat(Types.nullValue(String.class)).isNull();
  }
}
