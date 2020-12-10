/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ScoreComponentTest extends AbstractComponentTest<ScoreComponent, ScoreComponent.Builder> {
  @Override
  ScoreComponent.Builder builder() {
    return Component.scoreBuilder().name("test").objective("test");
  }

  @Test
  void testOf() {
    final ScoreComponent component = Component.score("abc", "def");
    assertEquals("abc", component.name());
    assertEquals("def", component.objective());
    assertNull(component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testName() {
    final ScoreComponent c0 = Component.score("abc", "def");
    final ScoreComponent c1 = c0.name("ghi");
    assertEquals("abc", c0.name());
    assertEquals("ghi", c1.name());
    assertEquals("def", c1.objective());
  }

  @Test
  void testObjective() {
    final ScoreComponent c0 = Component.score("abc", "def");
    final ScoreComponent c1 = c0.objective("ghi");
    assertEquals("def", c0.objective());
    assertEquals("ghi", c1.objective());
    assertEquals("abc", c1.name());
  }

  @Test
  void testValue() {
    final ScoreComponent c0 = Component.score("abc", "def");
    final ScoreComponent c1 = c0.value("ghi");
    assertNull(c0.value());
    assertEquals("ghi", c1.value());
  }
}
