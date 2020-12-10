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
package net.kyori.adventure.serializer.configurate3;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import ninja.leaping.configurate.ConfigurationNode;
import org.junit.jupiter.api.Test;

class HoverEventSerializersTest implements ConfigurateTestBase {
  @Test
  void testShowEntity() {
    final ConfigurationNode node = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TEXT).setValue("kashike");
      n.getNode(StyleSerializer.HOVER_EVENT).act(event -> {
        event.getNode(StyleSerializer.HOVER_EVENT_ACTION).setValue("show_entity");
        event.getNode(StyleSerializer.HOVER_EVENT_CONTENTS).act(entity -> {
          entity.getNode(HoverEventShowEntitySerializer.ENTITY_TYPE).setValue("minecraft:cat");
          entity.getNode(HoverEventShowEntitySerializer.ID).setValue("eb121687-8b1a-4944-bd4d-e0a818d9dfe2");
        });
      });
    });
    final Component component = Component.textBuilder().content("kashike")
      .hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.of(Key.key("minecraft:cat"), UUID.fromString("eb121687-8b1a-4944-bd4d-e0a818d9dfe2"))))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowEntityCustomName() {
    final ConfigurationNode node = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TEXT).setValue("kashike");
      n.getNode(StyleSerializer.HOVER_EVENT).act(event -> {
        event.getNode(StyleSerializer.HOVER_EVENT_ACTION).setValue("show_entity");
        event.getNode(StyleSerializer.HOVER_EVENT_CONTENTS).act(entity -> {
          entity.getNode(HoverEventShowEntitySerializer.ENTITY_TYPE).setValue("minecraft:cat");
          entity.getNode(HoverEventShowEntitySerializer.ID).setValue("eb121687-8b1a-4944-bd4d-e0a818d9dfe2");
          entity.getNode(HoverEventShowEntitySerializer.NAME, ComponentTypeSerializer.TEXT).setValue("meow");
        });
      });
    });
    final Component component = Component.textBuilder().content("kashike")
      .hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.of(Key.key("minecraft:cat"), UUID.fromString("eb121687-8b1a-4944-bd4d-e0a818d9dfe2"), Component.text("meow"))))
      .build();

    this.assertRoundtrippable(component, node);
  }

  @Test
  void testShowItem() {
    final ConfigurationNode node = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TEXT).setValue("[");
      n.getNode(StyleSerializer.COLOR).setValue("aqua");
      n.getNode(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().getNode(ComponentTypeSerializer.TRANSLATE).setValue("item.minecraft.purple_wool");
        extra.appendListNode().getNode(ComponentTypeSerializer.TEXT).setValue("]");
      });
      n.getNode(StyleSerializer.HOVER_EVENT).act(hover -> {
        hover.getNode(StyleSerializer.HOVER_EVENT_ACTION).setValue("show_item");
        hover.getNode(StyleSerializer.HOVER_EVENT_CONTENTS).act(action -> {
          action.getNode(HoverEventShowItemSerializer.ID).setValue("minecraft:purple_wool");
          action.getNode(HoverEventShowItemSerializer.COUNT).setValue(2);
          action.getNode(HoverEventShowItemSerializer.TAG).setValue("{Damage: 5b}");
        });
      });
    });
    final Component component = Component.textBuilder().content("[")
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
      n.getNode(ComponentTypeSerializer.TEXT).setValue("[");
      n.getNode(StyleSerializer.COLOR).setValue("aqua");
      n.getNode(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().getNode(ComponentTypeSerializer.TRANSLATE).setValue("item.minecraft.purple_wool");
        extra.appendListNode().getNode(ComponentTypeSerializer.TEXT).setValue("]");
      });
      n.getNode(StyleSerializer.HOVER_EVENT).act(hover -> {
        hover.getNode(StyleSerializer.HOVER_EVENT_ACTION).setValue("show_item");
        hover.getNode(StyleSerializer.HOVER_EVENT_CONTENTS).act(action -> {
          action.getNode(HoverEventShowItemSerializer.ID).setValue("minecraft:purple_wool");
          action.getNode(HoverEventShowItemSerializer.COUNT).setValue(1);
        });
      });
    });
    final Component component = Component.textBuilder().content("[")
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
      n.getNode(StyleSerializer.HOVER_EVENT).act(event -> {
        event.getNode(StyleSerializer.HOVER_EVENT_ACTION).setValue("show_text");
        event.getNode(StyleSerializer.HOVER_EVENT_CONTENTS, ComponentTypeSerializer.TEXT).setValue("i'm hovering");
      });
      n.getNode(ComponentTypeSerializer.TRANSLATE).setValue("look.at.me");
    });
    final Component component = Component.translatable("look.at.me", Style.style(HoverEvent.showText(Component.text("i'm hovering"))));

    this.assertRoundtrippable(component, node);
  }
}
