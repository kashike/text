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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkingLegacyComponentSerializerTest {
  @Test
  void testSimpleFrom() {
    assertEquals(Component.text("foo"), LegacyComponentSerializer.builder().extractUrls().build().deserialize("foo"));
  }

  @Test
  void testBareUrl() {
    final String bareUrl = "https://www.example.com";
    final TextComponent expectedNonLinkify = Component.text(bareUrl);
    assertEquals(expectedNonLinkify, LegacyComponentSerializer.legacySection().deserialize(bareUrl));
    final TextComponent expectedBareUrl = Component.text(bareUrl)
      .clickEvent(ClickEvent.openUrl(bareUrl));
    assertEquals(expectedBareUrl, LegacyComponentSerializer.builder().extractUrls().build().deserialize(bareUrl));
  }

  @Test
  void testPrefixUrl() {
    final String bareUrl = "https://www.example.com";
    final String hasPrefix = "did you hear about https://www.example.com";
    final TextComponent expectedHasPrefix = Component.textBuilder().content("did you hear about ")
      .append(Component.text(bareUrl).clickEvent(ClickEvent.openUrl(bareUrl)))
      .build();
    assertEquals(expectedHasPrefix, LegacyComponentSerializer.builder().character('&').extractUrls().build().deserialize(hasPrefix));
  }

  @Test
  void testPrefixSuffixUrl() {
    final String bareUrl = "https://www.example.com";
    final String hasPrefixSuffix = "did you hear about https://www.example.com? they're really cool";
    final TextComponent expectedHasPrefixSuffix = Component.textBuilder().content("did you hear about ")
      .append(Component.text(bareUrl).clickEvent(ClickEvent.openUrl(bareUrl)))
      .append(Component.text("? they're really cool"))
      .build();
    assertEquals(expectedHasPrefixSuffix, LegacyComponentSerializer.builder().character('&').extractUrls().build().deserialize(hasPrefixSuffix));
  }

  @Test
  void testPrefixSuffixUrlAndColors() {
    final String bareUrl = "https://www.example.com";
    final String hasPrefixSuffixColors = "&adid you hear about &chttps://www.example.com? &9they're really cool";
    final TextComponent expectedHasPrefixSuffixColors = Component.textBuilder().content("")
      .append(Component.text("did you hear about ", NamedTextColor.GREEN))
      .append(Component.text(b -> b.append(Component.text("https://www.example.com").clickEvent(ClickEvent.openUrl(bareUrl)))
        .append(Component.text("? "))
        .color(NamedTextColor.RED)))
      .append(Component.text("they're really cool", NamedTextColor.BLUE))
      .build();
    assertEquals(expectedHasPrefixSuffixColors, LegacyComponentSerializer.builder().character('&').extractUrls().build().deserialize(hasPrefixSuffixColors));
  }

  @Test
  void testMultipleUrls() {
    final String manyUrls = "go to https://www.example.com and https://www.example.net for cat videos";
    final TextComponent expectedManyUrls = Component.textBuilder().content("go to ")
      .append(Component.text("https://www.example.com").clickEvent(ClickEvent.openUrl("https://www.example.com")))
      .append(Component.text(" and "))
      .append(Component.text("https://www.example.net").clickEvent(ClickEvent.openUrl("https://www.example.net")))
      .append(Component.text(" for cat videos"))
      .build();
    assertEquals(expectedManyUrls, LegacyComponentSerializer.builder().character('&').extractUrls().build().deserialize(manyUrls));
  }

  @Test
  void testLinkifyWithStyle() {
    final Style testStyle = Style.style(NamedTextColor.GREEN, TextDecoration.UNDERLINED);
    final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').extractUrls(testStyle).build();

    final String bareUrl = "https://www.example.com";
    final Component expectedBareUrl = Component.text(bareUrl).style(testStyle.clickEvent(ClickEvent.openUrl(bareUrl)));
    assertEquals(expectedBareUrl, serializer.deserialize(bareUrl));

    final String hasPrefixSuffix = "did you hear about https://www.example.com? they're really cool";
    final TextComponent expectedHasPrefixSuffix = Component.textBuilder().content("did you hear about ")
      .append(Component.text(bareUrl).style(testStyle.clickEvent(ClickEvent.openUrl(bareUrl))))
      .append(Component.text("? they're really cool"))
      .build();
    assertEquals(expectedHasPrefixSuffix, serializer.deserialize(hasPrefixSuffix));

    final String manyUrls = "go to https://www.example.com and https://www.example.net for cat videos";
    final TextComponent expectedManyUrls = Component.textBuilder().content("go to ")
      .append(Component.text("https://www.example.com").style(testStyle.clickEvent(ClickEvent.openUrl("https://www.example.com"))))
      .append(Component.text(" and "))
      .append(Component.text("https://www.example.net").style(testStyle.clickEvent(ClickEvent.openUrl("https://www.example.net"))))
      .append(Component.text(" for cat videos"))
      .build();
    assertEquals(expectedManyUrls, serializer.deserialize(manyUrls));
  }
}
