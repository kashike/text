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

import java.time.Duration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;

class TitleSerializerTest implements ConfigurateTestBase {
  @Test
  void testTitleNoTimes() {
    final ConfigurationNode node = this.node(n -> {
      n.node(TitleSerializer.TITLE, ComponentTypeSerializer.TEXT).raw("Title");
      n.node(TitleSerializer.SUBTITLE).act(sub -> {
        sub.node(ComponentTypeSerializer.TEXT).raw("Subtitle");
        sub.node(StyleSerializer.COLOR).raw("dark_purple");
      });
    });

    final Title title = Title.title(Component.text("Title"), Component.text("Subtitle", NamedTextColor.DARK_PURPLE));

    this.assertRoundtrippable(Title.class, title, node);
  }

  @Test
  void testTitleWithTimes() {
    final ConfigurationNode node = this.node(n -> {
      n.node(TitleSerializer.TITLE, ComponentTypeSerializer.TEXT).raw("Title");
      n.node(TitleSerializer.SUBTITLE).act(sub -> {
        sub.node(ComponentTypeSerializer.TEXT).raw("Subtitle");
        sub.node(StyleSerializer.COLOR).raw("dark_purple");
      });
      n.node(TitleSerializer.TIMES).act(times -> {
        times.node(TitleSerializer.FADE_IN).raw("PT50S");
        times.node(TitleSerializer.STAY).raw("PT20S");
        times.node(TitleSerializer.FADE_OUT).raw("PT50S");
      });
    });

    final Title title = Title.title(Component.text("Title"), Component.text("Subtitle", NamedTextColor.DARK_PURPLE),
      Title.Times.of(Duration.ofSeconds(50), Duration.ofSeconds(20), Duration.ofSeconds(50)));

    this.assertRoundtrippable(Title.class, title, node);
  }
}
