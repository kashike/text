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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import ninja.leaping.configurate.ConfigurationNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentSerializerTest implements ConfigurateTestBase {
  @Test
  void testTextComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TEXT).setValue("Hello world");
    });
    final Component component = Component.text("Hello world");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testTranslatableComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TRANSLATE).setValue("translation.string");
      n.getNode(ComponentTypeSerializer.TRANSLATE_WITH).act(w -> {
        w.appendListNode().getNode(ComponentTypeSerializer.TEXT).setValue("test1");
        w.appendListNode().getNode(ComponentTypeSerializer.TEXT).setValue("test2");
      });
    });
    final Component component = Component.translatable("translation.string", Component.text("test1"), Component.text("test2"));

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testScoreComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.SCORE).act(s -> {
        s.getNode(ComponentTypeSerializer.SCORE_NAME).setValue("Holder");
        s.getNode(ComponentTypeSerializer.SCORE_OBJECTIVE).setValue("some.objective");
        s.getNode(ComponentTypeSerializer.SCORE_VALUE).setValue("Override");
      });
    });
    final Component component = Component.score("Holder", "some.objective", "Override");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testScoreComponentNoValue() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.SCORE).act(s -> {
        s.getNode(ComponentTypeSerializer.SCORE_NAME).setValue("Holder");
        s.getNode(ComponentTypeSerializer.SCORE_OBJECTIVE).setValue("some.objective");
      });
    });

    final Component component = Component.score("Holder", "some.objective");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testKeybindComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.KEYBIND).setValue("key.worldeditcui.toggle");
    });
    final Component component = Component.keybind("key.worldeditcui.toggle");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testSelectorComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.SELECTOR).setValue("@e[limit=1]");
    });
    final Component component = Component.selector("@e[limit=1]");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testBlockNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.NBT).setValue("Something[1].CustomName");
      n.getNode(ComponentTypeSerializer.NBT_INTERPRET).setValue(true);
      n.getNode(ComponentTypeSerializer.NBT_BLOCK).setValue("^0.0 ^0.0 ^0.0");
    });
    final Component component = Component.blockNBTBuilder()
      .nbtPath("Something[1].CustomName")
      .interpret(true)
      .localPos(0, 0, 0)
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testEntityNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.NBT).setValue("Something[1].CustomName");
      n.getNode(ComponentTypeSerializer.NBT_INTERPRET).setValue(false);
      n.getNode(ComponentTypeSerializer.NBT_ENTITY).setValue("@e[limit=1]");
    });
    final Component component = Component.entityNBTBuilder()
      .nbtPath("Something[1].CustomName")
      .interpret(false)
      .selector("@e[limit=1]")
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testStorageNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.NBT).setValue("Kittens.Toes[0]");
      n.getNode(ComponentTypeSerializer.NBT_INTERPRET).setValue(false);
      n.getNode(ComponentTypeSerializer.NBT_STORAGE).setValue("adventure:purr");
    });
    final Component component = Component.storageNBTBuilder()
      .nbtPath("Kittens.Toes[0]")
      .interpret(false)
      .storage(Key.key("adventure", "purr"))
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testComponentWithChildren() {
    final ConfigurationNode serialized = this.node(n -> {
      n.getNode(ComponentTypeSerializer.TEXT).setValue("Hello");
      n.getNode(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().getNode(ComponentTypeSerializer.TRANSLATE).setValue("adventure.world");
        extra.appendListNode().getNode(ComponentTypeSerializer.KEYBIND).setValue("minecraft.key.jump");
      });
    });
    final Component component = Component.textBuilder().content("Hello")
      .append(Component.translatable("adventure.world"))
      .append(Component.keybind("minecraft.key.jump"))
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testArrayChildren() {
    final ConfigurationNode serialized = this.node(n -> {
      n.appendListNode().getNode(ComponentTypeSerializer.TEXT).setValue("first");
      n.appendListNode().act(child -> {
        child.getNode(ComponentTypeSerializer.TRANSLATE).setValue("keys.second");
        child.getNode(StyleSerializer.COLOR).setValue("#deadca");
      });
    });
    final Component deserialized = Component.text("first")
      .append(Component.translatable("keys.second", TextColor.color(0xdeadca)));

    assertEquals(deserialized, this.deserialize(serialized));
  }
}
