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

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BookSerializerTest implements ConfigurateTestBase {
  @Test
  void testBook() {
    final ConfigurationNode node = this.node(n -> {
      n.node(BookTypeSerializer.TITLE, ComponentTypeSerializer.TEXT).raw("My book");
      n.node(BookTypeSerializer.AUTHOR).act(author -> {
        author.node(StyleSerializer.FONT).raw("minecraft:uniform");
        author.node(ComponentTypeSerializer.TEXT).raw("myself");
      });
      n.node(BookTypeSerializer.PAGES).act(pages -> {
        pages.appendListNode().node(ComponentTypeSerializer.TEXT).raw("Page 1");
        pages.appendListNode().act(page -> {
          page.node(StyleSerializer.COLOR).raw("dark_red");
          page.node(ComponentTypeSerializer.TEXT).raw("Page 2");
        });
        pages.appendListNode().node(ComponentTypeSerializer.TEXT).raw("Page 3");
      });
    });
    final Book deserialized = Book.builder().title(Component.text("My book"))
      .author(Component.text("myself", Style.style().font(Key.key("uniform")).build()))
      .addPage(Component.text("Page 1"))
      .addPage(Component.text("Page 2", NamedTextColor.DARK_RED))
      .addPage(Component.text("Page 3"))
      .build();

    this.assertRoundtrippable(Book.class, deserialized, node);
  }

  @Test
  void testNoTitleFails() {
    final ConfigurationNode node = this.node(n -> {
      n.node(BookTypeSerializer.AUTHOR).act(author -> {
        author.node(StyleSerializer.FONT).raw("minecraft:uniform");
        author.node(ComponentTypeSerializer.TEXT).raw("myself");
      });
      n.node(BookTypeSerializer.PAGES).appendListNode().node(ComponentTypeSerializer.TEXT).raw("Page 1");
    });

    assertThrows(SerializationException.class, () -> node.get(Book.class));
  }

  @Test
  void testNoAuthorFails() {
    final ConfigurationNode node = this.node(n -> {
      n.node(BookTypeSerializer.TITLE, ComponentTypeSerializer.TEXT).raw("My book");
      n.node(BookTypeSerializer.PAGES).appendListNode().node(ComponentTypeSerializer.TEXT).raw("Page 1");
    });

    assertThrows(SerializationException.class, () -> node.get(Book.class));
  }
}
