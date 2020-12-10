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

import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearComponentsTest {
  @Test
  void testEmpty() {
    assertSame(Component.empty(), LinearComponents.linear());
  }

  @Test
  void testNothingComponentLike() {
    assertThrows(IllegalStateException.class, () -> LinearComponents.linear(TextDecoration.BOLD));
    assertThrows(IllegalStateException.class, () -> LinearComponents.linear(TextDecoration.BOLD, TextColor.color(0xaa0000)));
  }

  @Test
  void testSingleComponentLike() {
    final Component c0 = Component.text("kittens");
    assertSame(c0, LinearComponents.linear(c0));
  }

  @Test
  void testSimpleText() {
    final Component c0 = Component.text("kittens", NamedTextColor.DARK_PURPLE);
    assertEquals(c0, LinearComponents.linear(NamedTextColor.DARK_PURPLE, Component.textBuilder().content("kittens")));
  }

  @Test
  void testAdvancedText() {
    final Component c0 = Component.textBuilder()
      .append(Component.text("kittens", NamedTextColor.DARK_PURPLE))
      .append(Component.text("cats", Style.style(NamedTextColor.DARK_AQUA, TextDecoration.BOLD, HoverEvent.showText(Component.text("are adorable!")))))
      .build();
    assertEquals(c0, LinearComponents.linear(
      NamedTextColor.DARK_PURPLE, Component.textBuilder().content("kittens"),
      NamedTextColor.DARK_AQUA, TextDecoration.BOLD, HoverEvent.showText(Component.text("are adorable!")), Component.textBuilder().content("cats")
    ));
  }
}
