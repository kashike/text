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

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.NBTComponentBuilder;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class ComponentTypeSerializer implements TypeSerializer<Component> {
  static final TypeToken<List<Component>> LIST_TYPE = new TypeToken<List<Component>>() {};
  static final String TEXT = "text";
  static final String TRANSLATE = "translate";
  static final String TRANSLATE_WITH = "with";
  static final String SCORE = "score";
  static final String SCORE_NAME = "name";
  static final String SCORE_OBJECTIVE = "objective";
  static final String SCORE_VALUE = "value";
  static final String SELECTOR = "selector";
  static final String KEYBIND = "keybind";
  static final String EXTRA = "extra";
  static final String NBT = "nbt";
  static final String NBT_INTERPRET = "interpret";
  static final String NBT_BLOCK = "block";
  static final String NBT_ENTITY = "entity";
  static final String NBT_STORAGE = "storage";

  private final @Nullable ComponentSerializer<Component, ? extends Component, String> stringSerial;
  private final boolean preferString;

  ComponentTypeSerializer(final @Nullable ComponentSerializer<Component, ? extends Component, String> stringSerial, final boolean preferString) {
    this.stringSerial = stringSerial;
    this.preferString = preferString;
  }

  @Override
  public @NonNull Component deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    return this.deserialize0(type, value);
  }

  private @NonNull BuildableComponent<?, ?> deserialize0(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    // Try to read as a string
    if(!value.isList() && !value.isMap()) {
      final String str = value.getString();
      if(str != null) {
        if(this.stringSerial != null) {
          final Component ret = this.stringSerial.deserialize(str);
          if(!(ret instanceof BuildableComponent<?, ?>)) {
            throw new SerializationException("Result " + ret + " is not builable");
          }
          return (BuildableComponent<?, ?>) ret;
        } else {
          return Component.text(str);
        }
      }
    } else if(value.isList()) {
      ComponentBuilder<?, ?> parent = null;
      for(final ConfigurationNode childElement : value.childrenList()) {
        final BuildableComponent<?, ?> child = this.deserialize0(Component.class, childElement);
        if(parent == null) {
          parent = child.toBuilder();
        } else {
          parent.append(child);
        }
      }
      if(parent == null) {
        throw notSureHowToDeserialize(value);
      }
      return parent.build();
    } else if(!value.isMap()) {
      throw notSureHowToDeserialize(value);
    }

    final ComponentBuilder<?, ?> component;
    final Map<Object, ? extends ConfigurationNode> children = value.childrenMap();
    if(children.containsKey(TEXT)) {
      component = Component.text().content(children.get(TEXT).getString());
    } else if(children.containsKey(TRANSLATE)) {
      final String key = children.get(TRANSLATE).getString();
      if(!children.containsKey(TRANSLATE_WITH)) {
        component = Component.translatable().key(key);
      } else {
        final ConfigurationNode with = children.get(TRANSLATE_WITH);
        if(!with.isList()) {
          throw new SerializationException("Expected " + TRANSLATE_WITH + " to be a list");
        }
        final List<Component> args = with.get(LIST_TYPE);
        component = Component.translatable().key(key).args(args);
      }
    } else if(children.containsKey(SCORE)) {
      final ConfigurationNode score = children.get(SCORE);
      final ConfigurationNode name = score.node(SCORE_NAME);
      final ConfigurationNode objective = score.node(SCORE_OBJECTIVE);
      if(name.virtual() || objective.virtual()) {
        throw new SerializationException("A score component requires a " + SCORE_NAME + " and " + SCORE_OBJECTIVE);
      }
      final ScoreComponent.Builder builder = Component.score()
        .name(name.getString())
        .objective(objective.getString());
      // score components can have a value sometimes, let's grab it
      final ConfigurationNode scoreValue = score.node(SCORE_VALUE);
      if(!scoreValue.virtual()) {
        component = builder.value(scoreValue.getString());
      } else {
        component = builder;
      }
    } else if(children.containsKey(SELECTOR)) {
      component = Component.selector().pattern(children.get(SELECTOR).getString());
    } else if(children.containsKey(KEYBIND)) {
      component = Component.keybind().keybind(children.get(KEYBIND).getString());
    } else if(children.containsKey(NBT)) {
      final String nbt = children.get(NBT).getString();
      final boolean interpret = children.containsKey(NBT_INTERPRET) && children.get(NBT_INTERPRET).getBoolean();
      if(children.containsKey(NBT_BLOCK)) {
        final BlockNBTComponent.Pos pos = children.get(NBT_BLOCK).get(BlockNBTPosSerializer.INSTANCE.type());
        component = nbt(Component.blockNBT(), nbt, interpret).pos(pos);
      } else if(children.containsKey(NBT_ENTITY)) {
        component = nbt(Component.entityNBT(), nbt, interpret).selector(children.get(NBT_ENTITY).getString());
      } else if(children.containsKey(NBT_STORAGE)) {
        component = nbt(Component.storageNBT(), nbt, interpret).storage(children.get(NBT_STORAGE).get(KeySerializer.INSTANCE.type()));
      } else {
        throw notSureHowToDeserialize(value);
      }
    } else {
      throw notSureHowToDeserialize(value);
    }

    if(children.containsKey(EXTRA)) {
      final ConfigurationNode extra = children.get(EXTRA);
      for(final ConfigurationNode child : extra.childrenList()) {
        component.append(this.deserialize0(Component.class, child));
      }
    }

    final Style style = value.get(Style.class, Style.empty());
    if(!style.isEmpty()) {
      component.style(style);
    }

    return component.build();
  }

  @Override
  public void serialize(final @NonNull Type type, final @Nullable Component src, final @NonNull ConfigurationNode value) throws SerializationException {
    value.set(null);
    if(src == null) {
      return;
    } else if(this.stringSerial != null && this.preferString) {
      try {
        value.set(this.stringSerial.serialize(src));
        return;
      } catch(final Exception ex) {
        throw new SerializationException(ex);
      }
    }
    if(src instanceof TextComponent) {
      value.node(TEXT).set(((TextComponent) src).content());
    } else if(src instanceof TranslatableComponent) {
      final TranslatableComponent tc = (TranslatableComponent) src;
      value.node(TRANSLATE).set(tc.key());
      if(!tc.args().isEmpty()) {
        final ConfigurationNode with = value.node(TRANSLATE_WITH);
        for(final Component arg : tc.args()) {
          with.appendListNode().set(Component.class, arg);
        }
      }
    } else if(src instanceof ScoreComponent) {
      final ScoreComponent sc = (ScoreComponent) src;
      final ConfigurationNode score = value.node(SCORE);
      score.node(SCORE_NAME).set(sc.name());
      score.node(SCORE_OBJECTIVE).set(sc.objective());
      // score component value is optional
      @SuppressWarnings("deprecation")
      final @Nullable String scoreValue = sc.value();
      if(scoreValue != null) score.node(SCORE_VALUE).set(scoreValue);
    } else if(src instanceof SelectorComponent) {
      value.node(SELECTOR).set(((SelectorComponent) src).pattern());
    } else if(src instanceof KeybindComponent) {
      value.node(KEYBIND).set(((KeybindComponent) src).keybind());
    } else if(src instanceof NBTComponent) {
      final NBTComponent<?, ?> nc = (NBTComponent<?, ?>) src;
      value.node(NBT).set(nc.nbtPath());
      value.node(NBT_INTERPRET).set(nc.interpret());
      if(src instanceof BlockNBTComponent) {
        value.node(NBT_BLOCK).set(BlockNBTPosSerializer.INSTANCE.type(), ((BlockNBTComponent) nc).pos());
      } else if(src instanceof EntityNBTComponent) {
        value.node(NBT_ENTITY).set(((EntityNBTComponent) nc).selector());
      } else if(src instanceof StorageNBTComponent) {
        value.node(NBT_STORAGE).set(KeySerializer.INSTANCE.type(), ((StorageNBTComponent) nc).storage());
      } else {
        throw notSureHowToSerialize(src);
      }
    } else {
      throw notSureHowToSerialize(src);
    }

    final List<Component> children = src.children();
    if(!children.isEmpty()) {
      value.node(EXTRA).set(LIST_TYPE, children);
    }

    if(src.hasStyling()) {
      // merge
      value.set(Style.class, src.style());
    }
  }

  private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(final B builder, final String nbt, final boolean interpret) {
    return builder
      .nbtPath(nbt)
      .interpret(interpret);
  }

  private static SerializationException notSureHowToDeserialize(final ConfigurationNode element) {
    return new SerializationException("Don't know how to turn " + element + " into a Component");
  }

  private static SerializationException notSureHowToSerialize(final Component component) {
    return new SerializationException("Don't know how to serialize " + component + " as a Component");
  }
}
