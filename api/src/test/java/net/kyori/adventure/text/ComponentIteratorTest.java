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
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ComponentIteratorTest {

  @Test
  public void testOf() {
    final int[] hits = {0};

    Component.text()
      .content("Purity Ring ")
      .append(Component.text("are absolutely ", NamedTextColor.DARK_PURPLE))
      .append(Component.text("the best "))
      .append(Component.translatable("some.adjective"))
      .append(Component.text(" band", NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(Component.text("ever"))))
      .append(Component.text("."))
      .build()
      .iterable(ComponentIteratorType.DEPTH_FIRST)
      .forEach(component -> hits[0]++);

    assertEquals(6, hits[0]);
  }

  @Test
  public void testOfEmpty() {
    for (final Component component : Component.empty().iterable(ComponentIteratorType.DEPTH_FIRST)) {
      if (component.equals(Component.empty())) {
        return;
      }
    }

    fail("root component wasn't located");
  }

  @Test
  public void testOfDfs() {
    final Component component = Component.text()
      .content("SKIP")
      .append(Component.text("SKIP").append(Component.text("DEEP")))
      .append(Component.text("WIDE"))
      .build();

    for (final Component inner : component.iterable(ComponentIteratorType.DEPTH_FIRST)) {
      if (inner instanceof TextComponent) {
        final String content = ((TextComponent) inner).content();

        if (content.equals("WIDE")) {
          fail("WIDE before DEEP");
          return;
        } else if (content.equals("DEEP")) {
          return;
        }
      }
    }

    fail("target component not found");
  }

  @Test
  public void testOfBfs() {
    final Component component = Component.text()
      .content("SKIP")
      .append(Component.text("SKIP").append(Component.text("DEEP")))
      .append(Component.text("WIDE"))
      .build();

    for (final Component inner : component.iterable(ComponentIteratorType.BREADTH_FIRST)) {
      if (inner instanceof TextComponent) {
        final String content = ((TextComponent) inner).content();

        if (content.equals("DEEP")) {
          fail("DEEP before WIDE");
          return;
        } else if (content.equals("WIDE")) {
          return;
        }
      }
    }

    fail("target component not found");
  }

  @Test
  public void testOfHover() {
    final Component component = Component.text()
      .append(Component.text("WITH TEXT").hoverEvent(Component.text("TEXT")))
      .append(Component.text("WITH ENTITY")
        .hoverEvent(HoverEvent.showEntity(Key.key("minecraft:pig"), UUID.randomUUID(), Component.text("ENTITY"))))
      .build();

    boolean foundText = false;
    boolean foundEntity = false;

    for (final Component inner : component.iterable(ComponentIteratorType.BREADTH_FIRST_WITH_HOVER)) {
      if (inner instanceof TextComponent) {
        final TextComponent text = (TextComponent) inner;

        if (text.content().equals("TEXT")) foundText = true;
        else if (text.content().equals("ENTITY")) foundEntity = true;
      }
    }

    assertTrue(foundText, "Could not locate text in component hover event.");
    assertTrue(foundEntity, "Could not locate entity display name in entity hover event.");
  }
}
