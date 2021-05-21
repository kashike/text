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
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import org.checkerframework.checker.nullness.qual.Nullable;

final class ComponentSerializerImpl extends TypeAdapter<Component> {
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

  static TypeAdapter<Component> withStyleSerializer(final TypeAdapter<Style> styleSerializer) {
    return new ComponentSerializerImpl(styleSerializer).nullSafe();
  }

  private final TypeAdapter<Style> styleSerializer;

  private ComponentSerializerImpl(final TypeAdapter<Style> styleSerializer) {
    this.styleSerializer = styleSerializer;
  }

  @Override
  public Component read(final JsonReader in) throws IOException {
    return this.deserialize(Streams.parse(in));
  }

  private BuildableComponent<?, ?> deserialize(final JsonElement element) throws JsonParseException {
    if(element.isJsonPrimitive()) {
      return Component.text(element.getAsString());
    } else if(element.isJsonArray()) {
      ComponentBuilder<?, ?> parent = null;
      for(final JsonElement childElement : element.getAsJsonArray()) {
        final BuildableComponent<?, ?> child = this.deserialize(childElement);
        if(parent == null) {
          parent = child.toBuilder();
        } else {
          parent.append(child);
        }
      }
      if(parent == null) {
        throw notSureHowToDeserialize(element);
      }
      return parent.build();
    } else if(!element.isJsonObject()) {
      throw notSureHowToDeserialize(element);
    }

    final JsonObject object = element.getAsJsonObject();
    final ComponentBuilder<?, ?> component;
    if(object.has(TEXT)) {
      component = Component.text().content(object.get(TEXT).getAsString());
    } else if(object.has(TRANSLATE)) {
      final String key = object.get(TRANSLATE).getAsString();
      if(!object.has(TRANSLATE_WITH)) {
        component = Component.translatable().key(key);
      } else {
        final JsonArray with = object.getAsJsonArray(TRANSLATE_WITH);
        final List<Component> args = new ArrayList<>(with.size());
        for(int i = 0, size = with.size(); i < size; i++) {
          final JsonElement argElement = with.get(i);
          args.add(this.deserialize(argElement));
        }
        component = Component.translatable().key(key).args(args);
      }
    } else if(object.has(SCORE)) {
      final JsonObject score = object.getAsJsonObject(SCORE);
      if(!score.has(SCORE_NAME) || !score.has(SCORE_OBJECTIVE)) {
        throw new JsonParseException("A score component requires a " + SCORE_NAME + " and " + SCORE_OBJECTIVE);
      }
      final ScoreComponent.Builder builder = Component.score()
        .name(score.get(SCORE_NAME).getAsString())
        .objective(score.get(SCORE_OBJECTIVE).getAsString());
      // score components can have a value sometimes, let's grab it
      if(score.has(SCORE_VALUE)) {
        component = builder.value(score.get(SCORE_VALUE).getAsString());
      } else {
        component = builder;
      }
    } else if(object.has(SELECTOR)) {
      component = Component.selector().pattern(object.get(SELECTOR).getAsString());
    } else if(object.has(KEYBIND)) {
      component = Component.keybind().keybind(object.get(KEYBIND).getAsString());
    } else if(object.has(NBT)) {
      final String nbt = object.get(NBT).getAsString();
      final boolean interpret = object.has(NBT_INTERPRET) && object.getAsJsonPrimitive(NBT_INTERPRET).getAsBoolean();
      if(object.has(NBT_BLOCK)) {
        final BlockNBTComponent.Pos pos = BlockNBTComponentPosSerializer.INSTANCE.fromJsonTree(object.get(NBT_BLOCK));
        component = nbt(Component.blockNBT(), nbt, interpret).pos(pos);
      } else if(object.has(NBT_ENTITY)) {
        component = nbt(Component.entityNBT(), nbt, interpret).selector(object.get(NBT_ENTITY).getAsString());
      } else if(object.has(NBT_STORAGE)) {
        component = nbt(Component.storageNBT(), nbt, interpret).storage(KeySerializer.INSTANCE.fromJsonTree(object.get(NBT_STORAGE)));
      } else {
        throw notSureHowToDeserialize(element);
      }
    } else {
      throw notSureHowToDeserialize(element);
    }

    if(object.has(EXTRA)) {
      final JsonArray extra = object.getAsJsonArray(EXTRA);
      for(int i = 0, size = extra.size(); i < size; i++) {
        final JsonElement extraElement = extra.get(i);
        component.append(this.deserialize(extraElement));
      }
    }

    final Style style = this.styleSerializer.fromJsonTree(element);
    if(!style.isEmpty()) {
      component.style(style);
    }

    return component.build();
  }

  private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(final B builder, final String nbt, final boolean interpret) {
    return builder
      .nbtPath(nbt)
      .interpret(interpret);
  }

  @Override
  public void write(final JsonWriter out, final Component value) throws IOException {
    Streams.write(this.serialize(value), out);
  }

  private JsonElement serialize(final Component src) {
    final JsonObject object = new JsonObject();

    if(src.hasStyling()) {
      final JsonElement style = this.styleSerializer.toJsonTree(src.style());
      if(style.isJsonObject()) {
        for(final Map.Entry<String, JsonElement> entry : ((JsonObject) style).entrySet()) {
          object.add(entry.getKey(), entry.getValue());
        }
      }
    }

    final List<Component> children = src.children();
    if(!children.isEmpty()) {
      final JsonArray extra = new JsonArray();
      for(final Component child : children) {
        extra.add(this.serialize(child));
      }
      object.add(EXTRA, extra);
    }

    if(src instanceof TextComponent) {
      object.addProperty(TEXT, ((TextComponent) src).content());
    } else if(src instanceof TranslatableComponent) {
      final TranslatableComponent tc = (TranslatableComponent) src;
      object.addProperty(TRANSLATE, tc.key());
      if(!tc.args().isEmpty()) {
        final JsonArray with = new JsonArray();
        for(final Component arg : tc.args()) {
          with.add(this.serialize(arg));
        }
        object.add(TRANSLATE_WITH, with);
      }
    } else if(src instanceof ScoreComponent) {
      final ScoreComponent sc = (ScoreComponent) src;
      final JsonObject score = new JsonObject();
      score.addProperty(SCORE_NAME, sc.name());
      score.addProperty(SCORE_OBJECTIVE, sc.objective());
      // score component value is optional
      @SuppressWarnings("deprecation")
      final @Nullable String value = sc.value();
      if(value != null) score.addProperty(SCORE_VALUE, value);
      object.add(SCORE, score);
    } else if(src instanceof SelectorComponent) {
      object.addProperty(SELECTOR, ((SelectorComponent) src).pattern());
    } else if(src instanceof KeybindComponent) {
      object.addProperty(KEYBIND, ((KeybindComponent) src).keybind());
    } else if(src instanceof NBTComponent) {
      final NBTComponent<?, ?> nc = (NBTComponent<?, ?>) src;
      object.addProperty(NBT, nc.nbtPath());
      object.addProperty(NBT_INTERPRET, nc.interpret());
      if(src instanceof BlockNBTComponent) {
        final JsonElement position = BlockNBTComponentPosSerializer.INSTANCE.toJsonTree(((BlockNBTComponent) nc).pos());
        object.add(NBT_BLOCK, position);
      } else if(src instanceof EntityNBTComponent) {
        object.addProperty(NBT_ENTITY, ((EntityNBTComponent) nc).selector());
      } else if(src instanceof StorageNBTComponent) {
        object.add(NBT_STORAGE, KeySerializer.INSTANCE.toJsonTree(((StorageNBTComponent) nc).storage()));
      } else {
        throw notSureHowToSerialize(src);
      }
    } else {
      throw notSureHowToSerialize(src);
    }

    return object;
  }

  static JsonParseException notSureHowToDeserialize(final Object element) {
    return new JsonParseException("Don't know how to turn " + element + " into a Component");
  }

  private static IllegalArgumentException notSureHowToSerialize(final Component component) {
    return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
  }
}
