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
package net.kyori.adventure.text.serializer.gson;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.serializer.gson.StyleTest.name;

class TranslatableComponentTest extends ComponentTest {
  private static final String KEY = "multiplayer.player.left";

  @Test
  void testNoArgs() {
    this.test(Component.translatable(KEY), object(json -> json.addProperty(ComponentSerializerImpl.TRANSLATE, KEY)));
  }

  @Test
  void testSingleArgWithEvents() {
    final UUID id = UUID.fromString("eb121687-8b1a-4944-bd4d-e0a818d9dfe2");
    final String name = "kashike";
    final String command = String.format("/msg %s ", name);

    this.test(
      Component.translatable(
        KEY,
        Component.textBuilder().content(name)
          .clickEvent(ClickEvent.suggestCommand(command))
          .hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.of(
            Key.key("minecraft", "player"),
            id,
            Component.text(name)
          )))
          .build()
      ).color(NamedTextColor.YELLOW),
      object(json -> {
        json.addProperty(ComponentSerializerImpl.TRANSLATE, KEY);
        json.addProperty(StyleSerializer.COLOR, name(NamedTextColor.YELLOW));
        json.add(ComponentSerializerImpl.TRANSLATE_WITH, array(with -> with.add(object(item -> {
          item.addProperty(ComponentSerializerImpl.TEXT, name);
          item.add(StyleSerializer.CLICK_EVENT, object(event -> {
            event.addProperty(StyleSerializer.CLICK_EVENT_ACTION, name(ClickEvent.Action.SUGGEST_COMMAND));
            event.addProperty(StyleSerializer.CLICK_EVENT_VALUE, command);
          }));
          item.add(StyleSerializer.HOVER_EVENT, object(event -> {
            event.addProperty(StyleSerializer.HOVER_EVENT_ACTION, name(HoverEvent.Action.SHOW_ENTITY));
            event.add(StyleSerializer.HOVER_EVENT_CONTENTS, object(value -> {
              value.addProperty(ShowEntitySerializer.TYPE, "minecraft:player");
              value.addProperty(ShowEntitySerializer.ID, id.toString());
              value.add(ShowEntitySerializer.NAME, object(namej -> {
                namej.addProperty(ComponentSerializerImpl.TEXT, name);
              }));
            }));
          }));
        }))));
      })
    );
  }
}
