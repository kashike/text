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
package net.kyori.adventure.serializer.configurate4;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleSerializerTest implements ConfigurateTestBase {
  @Test
  void testSerializeFont() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.FONT).raw("adventure:meow");
    });
    final Style style = Style.style()
      .font(Key.key("adventure", "meow"))
      .build();

    this.assertRoundtrippable(Style.class, style, node);
  }

  @Test
  void testSerializeHexColor() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.COLOR).raw("#123456");
    });
    final Style style = Style.style()
      .color(TextColor.color(0x123456))
      .build();

    this.assertRoundtrippable(Style.class, style, node);
  }

  @Test
  void testSerializeNumericColor() throws SerializationException {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.COLOR).raw(0x123456);
    });
    final Style style = Style.style()
      .color(TextColor.color(0x123456))
      .build();

    // we won't roundtrip to this format
    assertEquals(style, node.get(Style.class));
  }

  @Test
  void testSerializeNamedColor() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.COLOR).raw("dark_red");
    });
    final Style style = Style.style()
      .color(NamedTextColor.DARK_RED)
      .build();

    this.assertRoundtrippable(Style.class, style, node);
  }

  @Test
  void testSerializeInsertion() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.INSERTION).raw("i'd like to get a cat!");
    });
    final Style style = Style.style()
      .insertion("i'd like to get a cat!")
      .build();

    this.assertRoundtrippable(Style.class, style, node);
  }

  @Test
  void testSerializeClickEvent() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.CLICK_EVENT).act(event -> {
        event.node(StyleSerializer.CLICK_EVENT_ACTION).raw("open_url");
        event.node(StyleSerializer.CLICK_EVENT_VALUE).raw("https://kyori.net");
      });
    });
    final Style style = Style.style()
      .clickEvent(ClickEvent.openUrl("https://kyori.net"))
      .build();

    this.assertRoundtrippable(Style.class, style, node);
  }

  // Hover event tested in HoverEventSerializersTest
}
