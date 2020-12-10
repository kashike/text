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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KeybindComponentTest extends AbstractComponentTest<KeybindComponent, KeybindComponent.Builder> {
  @Override
  KeybindComponent.Builder builder() {
    return Component.keybindBuilder().keybind("key.jump");
  }

  @Test
  void testOf() {
    final KeybindComponent component = Component.keybind("key.jump");
    assertEquals("key.jump", component.keybind());
    assertNull(component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testOf_color() {
    final KeybindComponent component = Component.keybind("key.jump", NamedTextColor.GREEN);
    assertEquals("key.jump", component.keybind());
    assertEquals(NamedTextColor.GREEN, component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testOf_color_decorations() {
    final KeybindComponent component = Component.keybind("key.jump", NamedTextColor.GREEN, ImmutableSet.of(TextDecoration.BOLD));
    assertEquals("key.jump", component.keybind());
    assertEquals(NamedTextColor.GREEN, component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(TextDecoration.BOLD), ImmutableSet.of());
  }

  @Test
  void testMake() {
    final KeybindComponent component = Component.keybind(builder -> {
      builder.keybind("key.jump");
      builder.color(NamedTextColor.DARK_PURPLE);
    });
    assertEquals("key.jump", component.keybind());
    assertEquals(NamedTextColor.DARK_PURPLE, component.color());
  }

  @Test
  void testMake_content() {
    final KeybindComponent component = Component.keybind(builder -> builder.keybind("key.jump").color(NamedTextColor.DARK_PURPLE));
    assertEquals("key.jump", component.keybind());
    assertEquals(NamedTextColor.DARK_PURPLE, component.color());
  }

  @Test
  void testKeybind() {
    final KeybindComponent c0 = Component.keybind("key.jump");
    final KeybindComponent c1 = c0.keybind("key.up");
    assertEquals("key.jump", c0.keybind());
    assertEquals("key.up", c1.keybind());
  }
}
