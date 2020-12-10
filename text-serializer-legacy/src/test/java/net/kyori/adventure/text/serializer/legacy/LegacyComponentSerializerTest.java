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
package net.kyori.adventure.text.serializer.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyComponentSerializerTest {
  @Test
  void testSimpleFrom() {
    final TextComponent component = Component.text("foo");
    assertEquals(component, LegacyComponentSerializer.legacySection().deserialize("foo"));
  }

  @Test
  void testFromColor() {
    final TextComponent component = Component.textBuilder().content("")
      .append(Component.text("foo").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(Component.text("bar").color(NamedTextColor.BLUE))
      .build();

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&lfoo&9bar"));
  }

  @Test
  void testFromColorOverride() {
    final TextComponent component = Component.text("foo").color(NamedTextColor.BLUE);

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&9foo"));
  }

  @Test
  void testResetOverride() {
    final TextComponent component = Component.textBuilder().content("")
      .append(Component.text("foo").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(Component.text("bar").color(NamedTextColor.DARK_GRAY))
      .build();

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&lfoo&r&8bar"));
  }

  @Test
  void testCompound() {
    final TextComponent component = Component.textBuilder()
      .content("hi there ")
      .append(Component.textBuilder().content("this bit is green ")
        .color(NamedTextColor.GREEN)
        .build())
      .append(Component.text("this isn't ").style(Style.empty()))
      .append(Component.textBuilder().content("and woa, this is again")
        .color(NamedTextColor.GREEN)
        .build())
      .build();

    assertEquals("hi there &athis bit is green &rthis isn't &aand woa, this is again", LegacyComponentSerializer.legacy('&').serialize(component));
  }

  @Test
  void testToLegacy() {
    final TextComponent c1 = Component.textBuilder().content("hi")
      .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
      .append(
        Component.text("foo")
          .color(NamedTextColor.GREEN)
          .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
      )
      .append(
        Component.text("bar")
          .color(NamedTextColor.BLUE)
      )
      .append(Component.text("baz"))
      .build();
    assertEquals("§lhi§afoo§9§lbar§r§lbaz", LegacyComponentSerializer.legacySection().serialize(c1));

    final TextComponent c2 = Component.textBuilder()
      .content("")
      .color(NamedTextColor.YELLOW)
      .append(Component.textBuilder()
        .content("Hello ")
        .append(
          Component.textBuilder()
            .content("world")
            .color(NamedTextColor.GREEN)
            .build()
        )
        .append(Component.text("!")) // Should be yellow
        .build()
      )
      .build();
    assertEquals("§eHello §aworld§e!", LegacyComponentSerializer.legacySection().serialize(c2));

    final TextComponent c3 = Component.textBuilder()
      .content("")
      .decoration(TextDecoration.BOLD, true)
      .append(
        Component.textBuilder()
          .content("")
          .color(NamedTextColor.YELLOW)
          .append(Component.textBuilder()
            .content("Hello ")
            .append(
              Component.textBuilder()
                .content("world")
                .color(NamedTextColor.GREEN)
                .build()
            )
            .append(Component.text("!"))
            .build()
          )
          .build())
      .build();
    assertEquals("§e§lHello §a§lworld§r§e§l!§r", LegacyComponentSerializer.legacySection().serialize(c3));
  }

  @Test
  void testToLegacyWithHexColor() {
    final TextComponent c0 = Component.text("Kittens!", TextColor.color(0xffefd5));
    assertEquals("§#ffefd5Kittens!", LegacyComponentSerializer.builder().hexColors().build().serialize(c0));
  }

  @Test
  void testToLegacyWithHexColorDownsampling() {
    final TextComponent comp = Component.text("purr", TextColor.color(0xff0000));
    assertEquals("§4purr", LegacyComponentSerializer.builder().build().serialize(comp));
  }

  @Test
  void testFromLegacyWithHexColor() {
    final TextComponent component = Component.textBuilder().content("")
      .append(Component.text("pretty").color(TextColor.fromHexString("#ffb6c1")))
      .append(Component.text("in").color(TextColor.fromHexString("#ff69b4")).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(Component.text("pink").color(TextColor.fromHexString("#ffc0cb")))
      .build();
    assertEquals(component, LegacyComponentSerializer.builder().character('&').hexColors().build().deserialize("&#ffb6c1pretty&#ff69b4&lin&#ffc0cbpink"));
  }

  @Test
  void testToLegacyWithHexColorTerribleFormat() {
    final TextComponent c0 = Component.text("Kittens!", TextColor.color(0xffefd5));
    assertEquals("§x§f§f§e§f§d§5Kittens!", LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(c0));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormat() {
    final TextComponent expected = Component.text("Kittens!", TextColor.color(0xffefd5));
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§x§f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatMixed() {
    final TextComponent expected = Component.textBuilder().content("")
      .append(Component.text("Hugs and ", NamedTextColor.RED))
      .append(Component.text("Kittens!", TextColor.color(0xffefd5)))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§cHugs and §x§f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatEnsureProperLookahead() {
    final TextComponent expected = Component.textBuilder().content("")
      .append(Component.text("Hugs and ", NamedTextColor.RED))
      .append(Component.text("Kittens!", NamedTextColor.DARK_PURPLE))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§cHugs and §f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatEnsureMultipleColorsWork() {
    final TextComponent expected = Component.textBuilder().content("Happy with ")
      .append(Component.text("Lavender and ", TextColor.color(0x6b4668)))
      .append(Component.text("Cyan!", TextColor.color(0xffefd5)))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("Happy with §x§6§b§4§6§6§8Lavender and §x§f§f§e§f§d§5Cyan!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatHangingCharacter() {
    final TextComponent expected = Component.textBuilder().content("§x")
      .append(Component.text("Kittens!", NamedTextColor.YELLOW))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§x§eKittens!"));
  }

  // https://github.com/KyoriPowered/adventure/issues/108
  @Test
  void testFromLegacyWithNewline() {
    final TextComponent comp = Component.textBuilder().content("One: Test ")
      .append(Component.text("String\nTwo: ", NamedTextColor.GREEN))
      .append(Component.text("Test ", NamedTextColor.AQUA))
      .append(Component.text("String", NamedTextColor.GREEN))
      .build();
    final String in = "One: Test &aString\nTwo: &bTest &aString";
    assertEquals(comp, LegacyComponentSerializer.legacy('&').deserialize(in));
  }

  // https://github.com/KyoriPowered/adventure/issues/108
  @Test
  void testBeginningTextUnformatted() {
    final String input = "Test &cString";
    final TextComponent expected = Component.textBuilder().content("Test ")
      .append(Component.text("String", NamedTextColor.RED))
      .build();

    assertEquals(expected, LegacyComponentSerializer.legacy(LegacyComponentSerializer.AMPERSAND_CHAR).deserialize(input));
  }

  // https://github.com/KyoriPowered/adventure/issues/92
  @Test
  void testStackedFormattingFlags() {
    final String input = "§r§r§c§k||§e§lProfile§c§k||";
    final TextComponent output = Component.textBuilder().append(
      Component.text("||", Style.style(NamedTextColor.RED, TextDecoration.OBFUSCATED)),
      Component.text("Profile", Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
      Component.text("||", Style.style(NamedTextColor.RED, TextDecoration.OBFUSCATED))
    ).build();
    assertEquals(output, LegacyComponentSerializer.legacySection().deserialize(input));
  }

  @Test
  void testResetClearsColorInSameBlock() {
    final String input = "§c§rCleared";
    final TextComponent output = Component.text("Cleared");
    assertEquals(output, LegacyComponentSerializer.legacySection().deserialize(input));
  }

  @Test
  void testParseColourChar() {
    final LegacyFormat lf = LegacyComponentSerializer.parseChar('5');
    assertNotNull(lf);
    assertEquals(NamedTextColor.DARK_PURPLE, lf.color());
    assertNull(lf.decoration());
    assertFalse(lf.reset());
  }

  @Test
  void testParseDecorationChar() {
    final LegacyFormat lf = LegacyComponentSerializer.parseChar('l');
    assertNotNull(lf);
    assertNull(lf.color());
    assertEquals(TextDecoration.BOLD, lf.decoration());
    assertFalse(lf.reset());
  }

  @Test
  void testParseResetChar() {
    final LegacyFormat lf = LegacyComponentSerializer.parseChar('r');
    assertNotNull(lf);
    assertNull(lf.color());
    assertNull(lf.decoration());
    assertTrue(lf.reset());
  }
}
