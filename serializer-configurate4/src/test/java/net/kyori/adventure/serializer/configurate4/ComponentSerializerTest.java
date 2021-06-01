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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentSerializerTest implements ConfigurateTestBase {
  @Test
  void testSerializeToString() {
    final ConfigurationNode serialized = ConfigurateComponentSerializer.builder()
      .scalarSerializer(GsonComponentSerializer.gson())
      .outputStringComponents(true)
      .build()
      .serialize(Component.text("Hello", Style.style(TextDecoration.BOLD)));

    assertEquals("{\"bold\":true,\"text\":\"Hello\"}", serialized.getString());
  }

  @Test
  void testTextComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("Hello world");
    });
    final Component component = Component.text("Hello world");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testTranslatableComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.TRANSLATE).raw("translation.string");
      n.node(ComponentTypeSerializer.TRANSLATE_WITH).act(w -> {
        w.appendListNode().node(ComponentTypeSerializer.TEXT).raw("test1");
        w.appendListNode().node(ComponentTypeSerializer.TEXT).raw("test2");
      });
    });
    final Component component = Component.translatable("translation.string", Component.text("test1"), Component.text("test2"));

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testScoreComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.SCORE).act(s -> {
        s.node(ComponentTypeSerializer.SCORE_NAME).raw("Holder");
        s.node(ComponentTypeSerializer.SCORE_OBJECTIVE).raw("some.objective");
        s.node(ComponentTypeSerializer.SCORE_VALUE).raw("Override");
      });
    });
    final Component component = Component.score("Holder", "some.objective", "Override");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testScoreComponentNoValue() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.SCORE).act(s -> {
        s.node(ComponentTypeSerializer.SCORE_NAME).raw("Holder");
        s.node(ComponentTypeSerializer.SCORE_OBJECTIVE).raw("some.objective");
      });
    });

    final Component component = Component.score("Holder", "some.objective");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testKeybindComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.KEYBIND).raw("key.worldeditcui.toggle");
    });
    final Component component = Component.keybind("key.worldeditcui.toggle");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testSelectorComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.SELECTOR).raw("@e[limit=1]");
    });
    final Component component = Component.selector("@e[limit=1]");

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testBlockNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.NBT).raw("Something[1].CustomName");
      n.node(ComponentTypeSerializer.NBT_INTERPRET).raw(true);
      n.node(ComponentTypeSerializer.NBT_BLOCK).raw("^0.0 ^0.0 ^0.0");
    });
    final Component component = Component.blockNBT()
      .nbtPath("Something[1].CustomName")
      .interpret(true)
      .localPos(0, 0, 0)
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testEntityNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.NBT).raw("Something[1].CustomName");
      n.node(ComponentTypeSerializer.NBT_INTERPRET).raw(false);
      n.node(ComponentTypeSerializer.NBT_ENTITY).raw("@e[limit=1]");
    });
    final Component component = Component.entityNBT()
      .nbtPath("Something[1].CustomName")
      .interpret(false)
      .selector("@e[limit=1]")
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testStorageNBTComponent() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.NBT).raw("Kittens.Toes[0]");
      n.node(ComponentTypeSerializer.NBT_INTERPRET).raw(false);
      n.node(ComponentTypeSerializer.NBT_STORAGE).raw("adventure:purr");
    });
    final Component component = Component.storageNBT()
      .nbtPath("Kittens.Toes[0]")
      .interpret(false)
      .storage(Key.key("adventure", "purr"))
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testComponentWithChildren() {
    final ConfigurationNode serialized = this.node(n -> {
      n.node(ComponentTypeSerializer.TEXT).raw("Hello");
      n.node(ComponentTypeSerializer.EXTRA).act(extra -> {
        extra.appendListNode().node(ComponentTypeSerializer.TRANSLATE).raw("adventure.world");
        extra.appendListNode().node(ComponentTypeSerializer.KEYBIND).raw("minecraft.key.jump");
      });
    });
    final Component component = Component.text().content("Hello")
      .append(Component.translatable("adventure.world"))
      .append(Component.keybind("minecraft.key.jump"))
      .build();

    this.assertRoundtrippable(component, serialized);
  }

  @Test
  void testArrayChildren() {
    final ConfigurationNode serialized = this.node(n -> {
      n.appendListNode().node(ComponentTypeSerializer.TEXT).raw("first");
      n.appendListNode().act(child -> {
        child.node(ComponentTypeSerializer.TRANSLATE).raw("keys.second");
        child.node(StyleSerializer.COLOR).raw("#deadca");
      });
    });
    final Component deserialized = Component.text("first")
      .append(Component.translatable("keys.second", TextColor.color(0xdeadca)));

    assertEquals(deserialized, this.deserialize(serialized));
  }
}
