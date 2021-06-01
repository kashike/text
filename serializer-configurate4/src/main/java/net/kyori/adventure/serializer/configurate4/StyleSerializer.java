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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class StyleSerializer implements TypeSerializer<Style> {
  static final StyleSerializer INSTANCE = new StyleSerializer();

  private static final TextDecoration[] DECORATIONS = TextDecoration.values();

  static final String FONT = "font";
  static final String COLOR = "color";
  static final String INSERTION = "insertion";
  static final String CLICK_EVENT = "clickEvent";
  static final String CLICK_EVENT_ACTION = "action";
  static final String CLICK_EVENT_VALUE = "value";
  static final String HOVER_EVENT = "hoverEvent";
  static final String HOVER_EVENT_ACTION = "action";
  static final TypeToken<HoverEvent.Action<?>> HOVER_EVENT_ACTION_TYPE = new TypeToken<HoverEvent.Action<?>>() {};
  static final String HOVER_EVENT_CONTENTS = "contents";
  static final @Deprecated String HOVER_EVENT_VALUE = "value";

  private StyleSerializer() {
  }

  @Override
  public @NonNull Style deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    if(value.virtual()) {
      return Style.empty();
    }

    final Style.Builder builder = Style.style();

    final @Nullable Key font = value.node(FONT).get(Key.class);
    if(font != null) {
      builder.font(font);
    }
    final @Nullable TextColor color = value.node(COLOR).get(TextColor.class);
    if(color != null) {
      builder.color(color);
    }

    for(final TextDecoration decoration : DECORATIONS) {
      final TextDecoration.State state = value.node(nonNull(TextDecoration.NAMES.key(decoration), "decoration")).get(TextDecoration.State.class);
      if(state != null) {
        builder.decoration(decoration, state);
      }
    }

    final @Nullable String insertion = value.node(INSERTION).getString();
    if(insertion != null) {
      builder.insertion(insertion);
    }

    final ConfigurationNode clickEvent = value.node(CLICK_EVENT);
    if(!clickEvent.virtual()) {
      final ClickEvent.Action action = nonNull(clickEvent.node(CLICK_EVENT_ACTION).get(ClickEvent.Action.class), "click event action");
      builder.clickEvent(ClickEvent.clickEvent(action, nonNull(clickEvent.node(CLICK_EVENT_VALUE).getString(), "click event value")));
    }

    final ConfigurationNode hoverEvent = value.node(HOVER_EVENT);
    if(!hoverEvent.virtual()) {
      final HoverEvent.Action<?> action = hoverEvent.node(HOVER_EVENT_ACTION).get(HOVER_EVENT_ACTION_TYPE);
      final ConfigurationNode contents = hoverEvent.node(HOVER_EVENT_CONTENTS);
      if(contents.virtual()) {
        final Component legacyValue = hoverEvent.node(HOVER_EVENT_VALUE).get(Component.class);
        if(legacyValue == null) {
          throw new SerializationException("No modern contents or legacy value present for hover event");
        }
        if(action == HoverEvent.Action.SHOW_TEXT) {
          builder.hoverEvent(HoverEvent.showText(legacyValue));
        } else {
          throw new SerializationException("Unable to deserialize legacy hover event of type " + action);
        }
        // TODO: Legacy hover event
      } else {
        if(action == HoverEvent.Action.SHOW_TEXT) {
          builder.hoverEvent(HoverEvent.showText(nonNull(contents.get(Component.class), "hover event text contents")));
        } else if(action == HoverEvent.Action.SHOW_ENTITY) {
          builder.hoverEvent(HoverEvent.showEntity(nonNull(contents.get(HoverEvent.ShowEntity.class), "hover event show entity contents")));
        } else if(action == HoverEvent.Action.SHOW_ITEM) {
          builder.hoverEvent(HoverEvent.showItem(nonNull(contents.get(HoverEvent.ShowItem.class), "hover event show item contents")));
        } else {
          throw new SerializationException("Unsupported hover event action " + action);
        }
      }
    }

    return builder.build();
  }

  @Override
  public void serialize(final @NonNull Type type, @Nullable Style obj, final @NonNull ConfigurationNode value) throws SerializationException {
    if(obj == null) {
      obj = Style.empty();
    }
    value.node(FONT).set(Key.class, obj.font());
    value.node(COLOR).set(TextColor.class, obj.color());
    for(final TextDecoration decoration : DECORATIONS) {
      final ConfigurationNode decorationNode = value.node(nonNull(TextDecoration.NAMES.key(decoration), "decoration"));
      final TextDecoration.State state = obj.decoration(decoration);
      if(state == TextDecoration.State.NOT_SET) {
        decorationNode.set(null);
      } else {
        decorationNode.set(state == TextDecoration.State.TRUE);
      }
    }
    value.node(INSERTION).set(obj.insertion());

    final ConfigurationNode clickNode = value.node(CLICK_EVENT);
    final ClickEvent clickEvent = obj.clickEvent();
    if(clickEvent == null) {
      clickNode.set(null);
    } else {
      clickNode.node(CLICK_EVENT_ACTION).set(ClickEvent.Action.class, clickEvent.action());
      clickNode.node(CLICK_EVENT_VALUE).set(clickEvent.value());
    }

    final ConfigurationNode hoverNode = value.node(HOVER_EVENT);
    if(obj.hoverEvent() == null) {
      hoverNode.set(null);
    } else {
      final HoverEvent<?> event = obj.hoverEvent();
      hoverNode.node(HOVER_EVENT_ACTION).set(HOVER_EVENT_ACTION_TYPE, event.action());
      final ConfigurationNode contentsNode = hoverNode.node(HOVER_EVENT_CONTENTS);
      if(event.action() == HoverEvent.Action.SHOW_TEXT) {
        contentsNode.set(Component.class, (Component) event.value());
      } else if(event.action() == HoverEvent.Action.SHOW_ENTITY) {
        contentsNode.set(HoverEvent.ShowEntity.class, (HoverEvent.ShowEntity) event.value());
      } else if(event.action() == HoverEvent.Action.SHOW_ITEM) {
        contentsNode.set(HoverEvent.ShowItem.class, (HoverEvent.ShowItem) event.value());
      }
    }
  }

  private static <T> @NonNull T nonNull(final @Nullable T value, final @NonNull String type) throws SerializationException {
    if(value == null) {
      throw new SerializationException(type + " was null in an unexpected location");
    }
    return value;
  }

  @Override
  public Style emptyValue(final Type specificType, final ConfigurationOptions options) {
    return Style.empty();
  }
}
