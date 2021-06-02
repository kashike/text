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

import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.stream.IntStream;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.TextAssertions.assertDecorations;
import static net.kyori.test.WeirdAssertions.assertAllEqualToEachOther;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextComponentTest extends AbstractComponentTest<TextComponent, TextComponent.Builder> {
  @Override
  TextComponent.Builder builder() {
    return Component.text().content("foo");
  }

  @Test
  void testNoWarningThrownWhenNoLegacyFormattingDetected() {
    final TextComponent component = Component.text("This is a test.");
    assertNull(((TextComponentImpl) component).warnWhenLegacyFormattingDetected());
  }

  @Test
  void testWarningThrownWhenLegacyFormattingDetected() {
    final TextComponent component = Component.text(legacy('3') + "This is a test.");
    assertNotNull(((TextComponentImpl) component).warnWhenLegacyFormattingDetected());
  }

  private static String legacy(final char character) {
    return TextComponentImpl.SECTION_CHAR + String.valueOf(character);
  }

  @Test
  void testOfChildren() {
    assertSame(Component.empty(), TextComponent.ofChildren()); // empty array
    assertEquals(
      Component.text()
        .append(Component.text("a"))
        .append(Component.text().content("b"))
        .build(),
      TextComponent.ofChildren(
        Component.text("a"),
        Component.text().content("b")
      )
    );
  }

  @Test
  void testOf() {
    final TextComponent component = Component.text("foo");
    assertEquals("foo", component.content());
    assertNull(component.color());
    assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testOfSameResult() {
    assertAllEqualToEachOther(
      Component.text("foo", Style.style(TextColor.color(0x0a1ab9))),
      Component.text("foo", TextColor.color(0x0a1ab9)),
      Component.text("foo", TextColor.color(0x0a1ab9), ImmutableSet.of())
    );
    assertAllEqualToEachOther(
      Component.text("foo", Style.style(TextColor.color(0x0a1ab9), TextDecoration.BOLD)),
      Component.text("foo", TextColor.color(0x0a1ab9), TextDecoration.BOLD),
      Component.text("foo", TextColor.color(0x0a1ab9), ImmutableSet.of(TextDecoration.BOLD))
    );
  }

  @Test
  void testOfKnownChar() {
    assertSame(Component.newline(), Component.text('\n'));
    assertSame(Component.space(), Component.text(' '));
  }

  @Test
  void testOf_color() {
    final TextComponent component = Component.text("foo", NamedTextColor.GREEN);
    assertEquals("foo", component.content());
    assertEquals(NamedTextColor.GREEN, component.color());
    assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testOf_color_decorations() {
    final TextComponent component = Component.text("foo", NamedTextColor.GREEN, ImmutableSet.of(TextDecoration.BOLD));
    assertEquals("foo", component.content());
    assertEquals(NamedTextColor.GREEN, component.color());
    assertDecorations(component, ImmutableSet.of(TextDecoration.BOLD), ImmutableSet.of());
  }

  @Test
  void testMake() {
    final TextComponent component = Component.text(builder -> {
      builder.content("foo");
      builder.color(NamedTextColor.DARK_PURPLE);
    });
    assertEquals("foo", component.content());
    assertEquals(NamedTextColor.DARK_PURPLE, component.color());
  }

  @Test
  void testContains() {
    final Component child = Component.text("kittens");
    final Component component = Component.text()
      .content("cat")
      .append(child)
      .build();
    assertTrue(component.contains(child));
  }

  @Test
  void testContent() {
    final TextComponent c0 = Component.text("foo");
    final TextComponent c1 = c0.content("bar");
    assertEquals("foo", c0.content());
    assertEquals("bar", c1.content());

    final TextComponent.Builder c2 = Component.text().content("hello world!");
    assertEquals("hello world!", c2.content());
  }

  @Test
  void testBuildEmptyIsEmpty() {
    assertSame(Component.empty(), Component.text().build());
  }

  @Test
  void testJoin() {
    assertEquals(Component.empty(), Component.join(Component.space(), Collections.emptyList()));

    final Component c0 = Component.join(
      Component.space(),
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0))
        .append(Component.space())
        .append(Component.text(1))
        .append(Component.space())
        .append(Component.text(2))
        .build(),
      c0
    );
  }
}
