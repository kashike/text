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

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;

class HoverEventSerializersTest implements ConfigurateTestBase {
  @Test
  void testShowEntity() {
    final ConfigurationNode node = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("kashike");
      n.node(StyleSerializer.HOVER_EVENT).act(event -> {
        event.node(StyleSerializer.HOVER_EVENT_ACTION).raw("show_entity");
        event.node(StyleSerializer.HOVER_EVENT_CONTENTS).act(entity -> {
          entity.node(HoverEventShowEntitySerializer.ENTITY_TYPE).raw("minecraft:cat");
          entity.node(HoverEventShowEntitySerializer.ID).raw("eb121687-8b1a-4944-bd4d-e0a818d9dfe2");
        });
      });
    });
    final Component component = Component.text().content("kashike")
      .hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.of(Key.key("minecraft:cat"), UUID.fromString("eb121687-8b1a-4944-bd4d-e0a818d9dfe2"))))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowEntityCustomName() {
    final ConfigurationNode node = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("kashike");
      n.node(StyleSerializer.HOVER_EVENT).act(event -> {
        event.node(StyleSerializer.HOVER_EVENT_ACTION).raw("show_entity");
        event.node(StyleSerializer.HOVER_EVENT_CONTENTS).act(entity -> {
          entity.node(HoverEventShowEntitySerializer.ENTITY_TYPE).raw("minecraft:cat");
          entity.node(HoverEventShowEntitySerializer.ID).raw("eb121687-8b1a-4944-bd4d-e0a818d9dfe2");
          entity.node(HoverEventShowEntitySerializer.NAME, ComponentTypeSerializer.TEXT).raw("meow");
        });
      });
    });
    final Component component = Component.text().content("kashike")
      .hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.of(Key.key("minecraft:cat"), UUID.fromString("eb121687-8b1a-4944-bd4d-e0a818d9dfe2"), Component.text("meow"))))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowItem() {
    final ConfigurationNode node = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("[");
      n.node(StyleSerializer.COLOR).raw("aqua");
      n.node(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().node(ComponentTypeSerializer.TRANSLATE).raw("item.minecraft.purple_wool");
        extra.appendListNode().node(ComponentTypeSerializer.TEXT).raw("]");
      });
      n.node(StyleSerializer.HOVER_EVENT).act(hover -> {
        hover.node(StyleSerializer.HOVER_EVENT_ACTION).raw("show_item");
        hover.node(StyleSerializer.HOVER_EVENT_CONTENTS).act(action -> {
          action.node(HoverEventShowItemSerializer.ID).raw("minecraft:purple_wool");
          action.node(HoverEventShowItemSerializer.COUNT).raw(2);
          action.node(HoverEventShowItemSerializer.TAG).raw("{Damage: 5b}");
        });
      });
    });
    final Component component = Component.text().content("[")
      .color(NamedTextColor.AQUA)
      .append(Component.translatable("item.minecraft.purple_wool"))
      .append(Component.text("]"))
      .hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.of(Key.key("minecraft:purple_wool"), 2, BinaryTagHolder.of("{Damage: 5b}"))))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowItemNoTag() {
    final ConfigurationNode node = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("[");
      n.node(StyleSerializer.COLOR).raw("aqua");
      n.node(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().node(ComponentTypeSerializer.TRANSLATE).raw("item.minecraft.purple_wool");
        extra.appendListNode().node(ComponentTypeSerializer.TEXT).raw("]");
      });
      n.node(StyleSerializer.HOVER_EVENT).act(hover -> {
        hover.node(StyleSerializer.HOVER_EVENT_ACTION).raw("show_item");
        hover.node(StyleSerializer.HOVER_EVENT_CONTENTS).act(action -> {
          action.node(HoverEventShowItemSerializer.ID).raw("minecraft:purple_wool");
          action.node(HoverEventShowItemSerializer.COUNT).raw(1);
        });
      });
    });
    final Component component = Component.text().content("[")
      .color(NamedTextColor.AQUA)
      .append(Component.translatable("item.minecraft.purple_wool"))
      .append(Component.text("]"))
      .hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.of(Key.key("minecraft:purple_wool"), 1)))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowText() {
    final ConfigurationNode node = this.node(n -> {
      n.node(StyleSerializer.HOVER_EVENT).act(event -> {
        event.node(StyleSerializer.HOVER_EVENT_ACTION).raw("show_text");
        event.node(StyleSerializer.HOVER_EVENT_CONTENTS, ComponentTypeSerializer.TEXT).raw("i'm hovering");
      });
      n.node(ComponentTypeSerializer.TRANSLATE).raw("look.at.me");
    });
    final Component component = Component.translatable("look.at.me", Style.style(HoverEvent.showText(Component.text("i'm hovering"))));

    this.assertRoundtrippable(component, node);
  }
}
