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

import com.google.common.reflect.TypeToken;
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
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("UnstableApiUsage") // TypeToken
final class ComponentTypeSerializer implements TypeSerializer<Component> {
  static final TypeToken<Component> TYPE = TypeToken.of(Component.class);
  @SuppressWarnings("serial")
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
  public @NonNull Component deserialize(final @NonNull TypeToken<?> type, final @NonNull ConfigurationNode value) throws ObjectMappingException {
    return this.deserialize0(type, value);
  }

  private @NonNull BuildableComponent<?, ?> deserialize0(final @NonNull TypeToken<?> type, final @NonNull ConfigurationNode value) throws ObjectMappingException {
    // Try to read as a string
    if(!value.isList() && !value.isMap()) {
      final String str = value.getString();
      if(str != null) {
        if(this.stringSerial != null) {
          final Component ret = this.stringSerial.deserialize(str);
          if(!(ret instanceof BuildableComponent<?, ?>)) {
            throw new ObjectMappingException("Result " + ret + " is not builable");
          }
          return (BuildableComponent<?, ?>) ret;
        } else {
          return Component.text(str);
        }
      }
    } else if(value.isList()) {
      ComponentBuilder<?, ?> parent = null;
      for(final ConfigurationNode childElement : value.getChildrenList()) {
        final BuildableComponent<?, ?> child = this.deserialize0(TYPE, childElement);
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
    final Map<Object, ? extends ConfigurationNode> children = value.getChildrenMap();
    if(children.containsKey(TEXT)) {
      component = Component.textBuilder().content(children.get(TEXT).getString());
    } else if(children.containsKey(TRANSLATE)) {
      final String key = children.get(TRANSLATE).getString();
      if(!children.containsKey(TRANSLATE_WITH)) {
        component = Component.translatableBuilder().key(key);
      } else {
        final ConfigurationNode with = children.get(TRANSLATE_WITH);
        if(!with.isList()) {
          throw new ObjectMappingException("Expected " + TRANSLATE_WITH + " to be a list");
        }
        final List<Component> args = with.getValue(LIST_TYPE);
        component = Component.translatableBuilder().key(key).args(args);
      }
    } else if(children.containsKey(SCORE)) {
      final ConfigurationNode score = children.get(SCORE);
      final ConfigurationNode name = score.getNode(SCORE_NAME);
      final ConfigurationNode objective = score.getNode(SCORE_OBJECTIVE);
      if(name.isVirtual() || objective.isVirtual()) {
        throw new ObjectMappingException("A score component requires a " + SCORE_NAME + " and " + SCORE_OBJECTIVE);
      }
      final ScoreComponent.Builder builder = Component.scoreBuilder()
        .name(name.getString())
        .objective(objective.getString());
      // score components can have a value sometimes, let's grab it
      final ConfigurationNode scoreValue = score.getNode(SCORE_VALUE);
      if(!scoreValue.isVirtual()) {
        component = builder.value(scoreValue.getString());
      } else {
        component = builder;
      }
    } else if(children.containsKey(SELECTOR)) {
      component = Component.selectorBuilder().pattern(children.get(SELECTOR).getString());
    } else if(children.containsKey(KEYBIND)) {
      component = Component.keybindBuilder().keybind(children.get(KEYBIND).getString());
    } else if(children.containsKey(NBT)) {
      final String nbt = children.get(NBT).getString();
      final boolean interpret = children.containsKey(NBT_INTERPRET) && children.get(NBT_INTERPRET).getBoolean();
      if(children.containsKey(NBT_BLOCK)) {
        final BlockNBTComponent.Pos pos = children.get(NBT_BLOCK).getValue(BlockNBTPosSerializer.INSTANCE.type());
        component = nbt(Component.blockNBTBuilder(), nbt, interpret).pos(pos);
      } else if(children.containsKey(NBT_ENTITY)) {
        component = nbt(Component.entityNBTBuilder(), nbt, interpret).selector(children.get(NBT_ENTITY).getString());
      } else if(children.containsKey(NBT_STORAGE)) {
        component = nbt(Component.storageNBTBuilder(), nbt, interpret).storage(children.get(NBT_STORAGE).getValue(KeySerializer.INSTANCE.type()));
      } else {
        throw notSureHowToDeserialize(value);
      }
    } else {
      throw notSureHowToDeserialize(value);
    }

    if(children.containsKey(EXTRA)) {
      final ConfigurationNode extra = children.get(EXTRA);
      for(final ConfigurationNode child : extra.getChildrenList()) {
        component.append(this.deserialize0(TYPE, child));
      }
    }

    final Style style = value.getValue(StyleSerializer.TYPE, Style.empty());
    if(!style.isEmpty()) {
      component.style(style);
    }

    return component.build();
  }

  @Override
  public void serialize(final @NonNull TypeToken<?> type, final @Nullable Component src, final @NonNull ConfigurationNode value) throws ObjectMappingException {
    value.setValue(null);
    if(src == null) {
      return;
    } else if(this.stringSerial != null && this.preferString) {
      try {
        value.setValue(this.stringSerial.serialize(src));
      } catch(final Exception ex) {
        throw new ObjectMappingException(ex);
      }
    }
    if(src instanceof TextComponent) {
      value.getNode(TEXT).setValue(((TextComponent) src).content());
    } else if(src instanceof TranslatableComponent) {
      final TranslatableComponent tc = (TranslatableComponent) src;
      value.getNode(TRANSLATE).setValue(tc.key());
      if(!tc.args().isEmpty()) {
        final ConfigurationNode with = value.getNode(TRANSLATE_WITH);
        for(final Component arg : tc.args()) {
          with.appendListNode().setValue(TYPE, arg);
        }
      }
    } else if(src instanceof ScoreComponent) {
      final ScoreComponent sc = (ScoreComponent) src;
      final ConfigurationNode score = value.getNode(SCORE);
      score.getNode(SCORE_NAME).setValue(sc.name());
      score.getNode(SCORE_OBJECTIVE).setValue(sc.objective());
      // score component value is optional
      final @Nullable String scoreValue = sc.value();
      if(scoreValue != null) score.getNode(SCORE_VALUE).setValue(scoreValue);
    } else if(src instanceof SelectorComponent) {
      value.getNode(SELECTOR).setValue(((SelectorComponent) src).pattern());
    } else if(src instanceof KeybindComponent) {
      value.getNode(KEYBIND).setValue(((KeybindComponent) src).keybind());
    } else if(src instanceof NBTComponent) {
      final NBTComponent<?, ?> nc = (NBTComponent<?, ?>) src;
      value.getNode(NBT).setValue(nc.nbtPath());
      value.getNode(NBT_INTERPRET).setValue(nc.interpret());
      if(src instanceof BlockNBTComponent) {
        value.getNode(NBT_BLOCK).setValue(BlockNBTPosSerializer.INSTANCE.type(), ((BlockNBTComponent) nc).pos());
      } else if(src instanceof EntityNBTComponent) {
        value.getNode(NBT_ENTITY).setValue(((EntityNBTComponent) nc).selector());
      } else if(src instanceof StorageNBTComponent) {
        value.getNode(NBT_STORAGE).setValue(KeySerializer.INSTANCE.type(), ((StorageNBTComponent) nc).storage());
      } else {
        throw notSureHowToSerialize(src);
      }
    } else {
      throw notSureHowToSerialize(src);
    }

    final List<Component> children = src.children();
    if(!children.isEmpty()) {
      value.getNode(EXTRA).setValue(LIST_TYPE, children);
    }

    if(src.hasStyling()) {
      // merge
      value.setValue(StyleSerializer.TYPE, src.style());
    }
  }

  private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(final B builder, final String nbt, final boolean interpret) {
    return builder
      .nbtPath(nbt)
      .interpret(interpret);
  }

  private static ObjectMappingException notSureHowToDeserialize(final ConfigurationNode element) {
    return new ObjectMappingException("Don't know how to turn " + element + " into a Component");
  }

  private static ObjectMappingException notSureHowToSerialize(final Component component) {
    return new ObjectMappingException("Don't know how to serialize " + component + " as a Component");
  }
}
