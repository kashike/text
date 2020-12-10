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

class EntityNBTComponentTest extends AbstractNBTComponentTest<EntityNBTComponent, EntityNBTComponent.Builder> {
  @Override
  EntityNBTComponent.Builder builder() {
    return Component.entityNBTBuilder().nbtPath("abc").selector("def");
  }

  @Test
  void testOf() {
    final EntityNBTComponent component = Component.entityNBT("abc", "def");
    assertEquals("abc", component.nbtPath());
    assertEquals("def", component.selector());
    assertNull(component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testSelector() {
    final EntityNBTComponent c0 = Component.entityNBT("abc", "def");
    final EntityNBTComponent c1 = c0.selector("ghi");
    assertEquals("def", c0.selector());
    assertEquals("ghi", c1.selector());
    assertEquals("abc", c1.nbtPath());
  }
}
