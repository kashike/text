/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2021 KyoriPowered
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

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.HoverEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// https://github.com/KyoriPowered/adventure/issues/363
class ComponentContainsTest {
  @Test
  public void testOf() {
    final Component c0 = Component.text("best!");
    final Component c1 = Component.text()
      .append(Component.text("Nero "))
      .append(Component.text(" are the ").append(c0))
      .build();

    assertTrue(c1.contains(c0));
    assertTrue(c1.contains(Component.text("best!"), Component.EQUALS));
    assertFalse(c1.contains(Component.newline()));
    assertFalse(c1.contains(Component.text("worst!"), Component.EQUALS));
  }

  @Test
  public void testOfHoverShowText() {
    final Component c0 = Component.text("A great EDM trio");
    final Component c1 = Component.text()
      .append(Component.text("Nero ").hoverEvent(c0))
      .append(Component.text(" are the best!"))
      .build();

    assertTrue(c1.contains(c0));
    assertTrue(c1.contains(Component.text("A great EDM trio"), Component.EQUALS));
  }

  @Test
  public void testOfHoverShowEntity() {
    final Component c0 = Component.text("Joe Ray");
    final Component c1 = Component.text()
      .append(Component.text("One member").hoverEvent(HoverEvent.showEntity(Key.key("minecraft:player"), UUID.randomUUID(), c0)))
      .append(Component.text(" launched a solo project in 2017."))
      .build();

    assertTrue(c1.contains(c0));
    assertTrue(c1.contains(Component.text("Joe Ray"), Component.EQUALS));
  }
}
