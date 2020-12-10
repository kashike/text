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
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.TextAssertions.assertDecorations;
import static net.kyori.test.WeirdAssertions.assertAllEqualToEachOther;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextComponentTest extends AbstractComponentTest<TextComponent, TextComponent.Builder> {
  @Override
  TextComponent.Builder builder() {
    return Component.textBuilder().content("foo");
  }

  @Test
  void testOfChildren() {
    assertSame(Component.empty(), TextComponent.ofChildren()); // empty array
    assertEquals(
      Component.textBuilder()
        .append(Component.text("a"))
        .append(Component.textBuilder().content("b"))
        .build(),
      TextComponent.ofChildren(
        Component.text("a"),
        Component.textBuilder().content("b")
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
    final Component component = Component.textBuilder()
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
  }

  @Test
  void testReplace() {
    final TextComponent component = Component.textBuilder()
      .content("cat says ")
      .append(Component.translatable("cat.meow")) // or any non-text component
      .build();
    final Component replaced = component.replaceText(b -> b.match("says").replacement(match -> match.color(NamedTextColor.DARK_PURPLE)));
    assertEquals(Component.textBuilder()
      .content("cat ")
      .append(Component.text("says", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" "))
      .append(Component.translatable("cat.meow"))
      .build(), replaced);
  }

  @Test
  void testReplaceFirst() {
    final TextComponent component = Component.textBuilder()
      .content("Buffalo buffalo Buffalo buffalo buffalo buffalo Buffalo buffalo ")
      .append(Component.translatable("buffalo.buffalo")) // or any non-text component
      .build();
    final Component replaced = component.replaceText(b -> b.match("buffalo").once().replacement(match -> match.color(NamedTextColor.DARK_PURPLE)));
    assertEquals(Component.textBuilder()
      .content("Buffalo ")
      .append(Component.text("buffalo", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" Buffalo buffalo buffalo buffalo Buffalo buffalo "))
      .append(Component.translatable("buffalo.buffalo"))
      .build(), replaced);
  }

  @Test
  void testReplaceN() {
    final TextComponent component = Component.textBuilder()
      .content("Buffalo buffalo Buffalo buffalo buffalo buffalo Buffalo buffalo ")
      .append(Component.translatable("buffalo.buffalo")) // or any non-text component
      .build();
    final Component replaced = component.replaceText(b -> b.match("buffalo").replacement(match -> match.color(NamedTextColor.DARK_PURPLE)).times(2));
    assertEquals(Component.textBuilder()
      .content("Buffalo ")
      .append(Component.text("buffalo", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" Buffalo "))
      .append(Component.text("buffalo", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" buffalo buffalo Buffalo buffalo "))
      .append(Component.translatable("buffalo.buffalo"))
      .build(), replaced);
  }

  @Test
  void testReplaceEveryOther() {
    final TextComponent component = Component.textBuilder()
      .content("purple purple purple purple purple purple purple purple ")
      .append(Component.translatable("purple.purple")) // or any non-text component
      .build();

    final Component replaced = component.replaceText(b -> b.match("purple")
      .replacement(match -> match.color(NamedTextColor.DARK_PURPLE))
      .condition((index, replace) -> index % 2 == 0 ? PatternReplacementResult.REPLACE : PatternReplacementResult.CONTINUE));

    assertEquals(Component.textBuilder()
      .content("purple ")
      .append(Component.text("purple", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" purple "))
      .append(Component.text("purple", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" purple "))
      .append(Component.text("purple", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" purple "))
      .append(Component.text("purple", NamedTextColor.DARK_PURPLE))
      .append(Component.text(" "))
      .append(Component.translatable("purple.purple"))
      .build(), replaced);
  }

  @Test
  void testReplaceNonStringComponents() {
    final TextComponent original = Component.textBuilder().content("Hello world")
      .append(Component.text("Child 1"), Component.keybind("key.adventure.purr"), Component.text("Child 3"))
      .build();
    final TextComponent expectedReplacement = Component.textBuilder().content("Hello world")
      .append(Component.textBuilder().append(Component.text("Parent")).append(Component.text(" 1")), Component.keybind("key.adventure.purr"), Component.textBuilder().append(Component.text("Parent")).append(Component.text(" 3")))
      .build();

    assertEquals(expectedReplacement, original.replaceText(b -> b.match("Child").replacement(match -> match.content("Parent"))));
  }

  // https://github.com/KyoriPowered/adventure/issues/129
  @Test
  void testReplaceWithChildren() {
    final TextComponent component = Component.textBuilder()
      .append(Component.text("This ").color(NamedTextColor.GRAY))
      .append(Component.text("is").color(NamedTextColor.BLUE)
        .append(Component.text(" the ").decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)))
      .append(Component.text("string").color(NamedTextColor.DARK_BLUE))
      .append(Component.text(" under test").color(NamedTextColor.DARK_AQUA))
      .build();
    final TextComponent expectedReplacement = Component.textBuilder()
      .append(Component.text("This ").color(NamedTextColor.GRAY))
      .append(Component.text("is").color(NamedTextColor.BLUE)
        .append(Component.text(" the ").decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)))
      .append(Component.text("string").color(NamedTextColor.DARK_BLUE))
      .append(Component.text(" under ").color(NamedTextColor.DARK_AQUA).append(Component.text("me")))
      .build();

    assertEquals(expectedReplacement, component.replaceText(b -> b.match("test").replacement("me")));
  }

  @Test
  void testReplaceInStyle() {
    final TextComponent component = Component.textBuilder().content("Hover on me")
      .hoverEvent(HoverEvent.showText(Component.text("meep", NamedTextColor.DARK_AQUA)))
      .build();
    final Component expectedReplacement = Component.textBuilder().content("Hover on me")
      .hoverEvent(HoverEvent.showText(Component.text("meow", NamedTextColor.DARK_RED)))
      .build();

    assertEquals(expectedReplacement, component.replaceText(b -> b.match("meep").replacement(builder -> builder.content("meow").color(NamedTextColor.DARK_RED))));
  }

  @Test
  void testReplaceTranslatableArgs() {
    final TextComponent component = Component.textBuilder().content("hello ")
      .append(Component.translatable("my.translation", Component.text("cats bad")))
      .build();
    final TextComponent expectedReplacement = Component.textBuilder().content("hello ")
      .append(Component.translatable("my.translation", Component.textBuilder().content("cats ").append(Component.text("good"))))
      .build();

    assertEquals(expectedReplacement, component.replaceText(b -> b.match(Pattern.compile("bad")).replacement(builder -> builder.content("good"))));
  }

  @Test
  void testPartialReplaceHasIsolatedStyle() {
    final TextComponent component = Component.text("Hello world", NamedTextColor.DARK_PURPLE);
    final TextComponent expectedReplacement = Component.text(b ->
      b.color(NamedTextColor.DARK_PURPLE)
      .append(Component.text("Goodbye", NamedTextColor.LIGHT_PURPLE))
      .append(Component.text(" world")));

    assertEquals(expectedReplacement, component.replaceText(b -> b.match(Pattern.compile("Hello")).replacement(builder -> builder.content("Goodbye").color(NamedTextColor.LIGHT_PURPLE))));
  }

  // https://github.com/KyoriPowered/adventure/issues/197
  @Test
  void testReplaceSameInComponentAndEvent() {
    final Component original = Component.text("/{0}", NamedTextColor.RED)
      .hoverEvent(Component.textBuilder()
        .append(Component.text("Click to run"))
        .append(Component.space())
        .append(Component.text("/{0}", NamedTextColor.RED)).build());

    final Component expected = Component.text("/", NamedTextColor.RED)
      .append(Component.text("mycommand"))
      .hoverEvent(Component.textBuilder()
        .append(Component.text("Click to run"))
        .append(Component.space())
      .append(Component.text("/", NamedTextColor.RED).append(Component.text("mycommand"))).build());

    final Pattern replaceWith = Pattern.compile("\\{(\\d+)}");

    final Component replaced = original.replaceText(builder ->
      builder.match(replaceWith)
        .replacement((matcher, comp) -> {
          assertEquals(0, Integer.parseInt(matcher.group(1))); // only one index in our test case

          return Component.text("mycommand");
        }));

    assertEquals(expected, replaced);
  }

  @Test
  void testBuildEmptyIsEmpty() {
    assertSame(Component.empty(), Component.textBuilder().build());
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
      Component.textBuilder()
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
