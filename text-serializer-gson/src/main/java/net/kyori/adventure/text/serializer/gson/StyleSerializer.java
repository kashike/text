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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Codec;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StyleSerializer extends TypeAdapter<Style> {
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  private static final TextDecoration[] DECORATIONS = {
    // The order here is important -- Minecraft does string comparisons of some
    // serialized components so we have to make sure our order matches Vanilla
    TextDecoration.BOLD,
    TextDecoration.ITALIC,
    TextDecoration.UNDERLINED,
    TextDecoration.STRIKETHROUGH,
    TextDecoration.OBFUSCATED
  };

  static {
    // Ensure coverage of decorations
    final Set<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
    for(final TextDecoration decoration : DECORATIONS) {
      knownDecorations.remove(decoration);
    }
    if(!knownDecorations.isEmpty()) {
      throw new IllegalStateException("GSON serializer is missing some text decorations: " + knownDecorations);
    }
  }

  static final String FONT = "font";
  static final String COLOR = "color";
  static final String INSERTION = "insertion";
  static final String CLICK_EVENT = "clickEvent";
  static final String CLICK_EVENT_ACTION = "action";
  static final String CLICK_EVENT_VALUE = "value";
  static final String HOVER_EVENT = "hoverEvent";
  static final String HOVER_EVENT_ACTION = "action";
  static final String HOVER_EVENT_CONTENTS = "contents";
  static final @Deprecated String HOVER_EVENT_VALUE = "value";

  private final LegacyHoverEventSerializer legacyHover;
  private final boolean emitLegacyHover;
  private final TypeAdapter<TextColor> colorSerializer;
  private final TypeAdapter<Component> componentSerializer;
  private final TypeAdapter<HoverEvent.ShowEntity> showEntitySerializer;

  StyleSerializer(final @Nullable LegacyHoverEventSerializer legacyHover, final boolean emitLegacyHover, final TypeAdapter<TextColor> colorSerializer) {
    this.legacyHover = legacyHover;
    this.emitLegacyHover = emitLegacyHover;
    this.colorSerializer = colorSerializer;
    this.componentSerializer = ComponentSerializerImpl.withStyleSerializer(this);
    this.showEntitySerializer = new ShowEntitySerializer(this.componentSerializer);
  }

  @Override
  public Style read(final JsonReader in) throws IOException {
    in.beginObject();
    final Style.Builder style = Style.style();

    while(in.hasNext()) {
      final String fieldName = in.nextName();
      if(fieldName.equals(FONT)) {
        style.font(KeySerializer.INSTANCE.read(in));
      } else if(fieldName.equals(COLOR)) {
        final TextColorWrapper color = TextColorWrapper.Serializer.INSTANCE.read(in);
        if(color.color != null) {
          style.color(color.color);
        } else if(color.decoration != null) {
          style.decoration(color.decoration, TextDecoration.State.TRUE);
        }
      } else if(TextDecoration.NAMES.keys().contains(fieldName)) {
        style.decoration(TextDecoration.NAMES.value(fieldName), in.nextBoolean());
      } else if(fieldName.equals(INSERTION)) {
        style.insertion(in.nextString());
      } else if(fieldName.equals(CLICK_EVENT)) {
        in.beginObject();
        ClickEvent.Action action = null;
        String value = null;
        while(in.hasNext()) {
          final String clickEventField = in.nextName();
          if(clickEventField.equals(CLICK_EVENT_ACTION)) {
            action = ClickEventActionSerializer.INSTANCE.read(in);
          } else if(clickEventField.equals(CLICK_EVENT_VALUE)) {
            value = in.peek() == JsonToken.NULL ? null : in.nextString();
          } else {
            in.skipValue();
          }
        }
        if(action != null && action.readable() && value != null) {
          style.clickEvent(ClickEvent.clickEvent(action, value));
        }
        in.endObject();
      } else if(fieldName.equals(HOVER_EVENT)) {
        final JsonObject hoverEventObject = Streams.parse(in).getAsJsonObject();
        if(hoverEventObject != null) {
          final HoverEvent.@Nullable Action action = this.optionallyDeserialize(hoverEventObject.getAsJsonPrimitive(HOVER_EVENT_ACTION), HoverEventActionSerializer.INSTANCE);
          if(action != null && action.readable()) {
            final @Nullable Object value;
            if(hoverEventObject.has(HOVER_EVENT_CONTENTS)) {
              final @Nullable JsonElement rawValue = hoverEventObject.get(HOVER_EVENT_CONTENTS);
              final Class<?> actionType = action.type();
              if(SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
                value = this.componentSerializer.fromJsonTree(rawValue);
              } else if(SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType)) {
                value = ShowItemSerializer.INSTANCE.fromJsonTree(rawValue);
              } else if(SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType)) {
                value = this.showEntitySerializer.fromJsonTree(rawValue);
              } else {
                value = null;
              }
            } else if(hoverEventObject.has(HOVER_EVENT_VALUE)) {
              final Component rawValue = this.componentSerializer.fromJsonTree(hoverEventObject.get(HOVER_EVENT_VALUE));
              value = this.legacyHoverEventContents(action, rawValue);
            } else {
              value = null;
            }

            if(value != null) {
              style.hoverEvent(HoverEvent.hoverEvent(action, value));
            }
          }
        }
      } else {
        in.skipValue();
      }
    }

    in.endObject();
    return style.build();
  }

  private <T> T optionallyDeserialize(final JsonElement json, final TypeAdapter<T> adapter) {
    return json == null ? null : adapter.fromJsonTree(json);
  }

  private Object legacyHoverEventContents(final HoverEvent.Action<?> action, final Component rawValue) {
    if(action == HoverEvent.Action.SHOW_TEXT) {
      return rawValue; // Passthrough -- no serialization needed
    } else if(this.legacyHover != null) {
      try {
        if(action == HoverEvent.Action.SHOW_ENTITY) {
          return this.legacyHover.deserializeShowEntity(rawValue, this.decoder());
        } else if(action == HoverEvent.Action.SHOW_ITEM) {
          return this.legacyHover.deserializeShowItem(rawValue);
        }
      } catch(final IOException ex) {
        throw new JsonParseException(ex);
      }
    }
    // if we can't handle
    throw new UnsupportedOperationException();
  }

  private Codec.Decoder<Component, String, JsonParseException> decoder() {
    return string -> {
      try {
        return this.componentSerializer.fromJson(string);
      } catch(final IOException ex) {
        throw new JsonParseException(ex);
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public void write(final JsonWriter out, final Style value) throws IOException {
    out.beginObject();

    for(int i = 0, length = DECORATIONS.length; i < length; i++) {
      final TextDecoration decoration = DECORATIONS[i];
      final TextDecoration.State state = value.decoration(decoration);
      if(state != TextDecoration.State.NOT_SET) {
        final String name = TextDecoration.NAMES.key(decoration);
        assert name != null; // should never be null
        out.name(name);
        out.value(state == TextDecoration.State.TRUE);
      }
    }

    final @Nullable TextColor color = value.color();
    if(color != null) {
      out.name(COLOR);
      this.colorSerializer.write(out, color);
    }

    final @Nullable String insertion = value.insertion();
    if(insertion != null) {
      out.name(INSERTION);
      out.value(insertion);
    }

    final @Nullable ClickEvent clickEvent = value.clickEvent();
    if(clickEvent != null) {
      out.name(CLICK_EVENT);
      out.beginObject();
      out.name(CLICK_EVENT_ACTION);
      ClickEventActionSerializer.INSTANCE.write(out, clickEvent.action());
      out.name(CLICK_EVENT_VALUE);
      out.value(clickEvent.value());
      out.endObject();
    }

    final @Nullable HoverEvent<?> hoverEvent = value.hoverEvent();
    if(hoverEvent != null) {
      out.name(HOVER_EVENT);
      out.beginObject();
      out.name(HOVER_EVENT_ACTION);
      final HoverEvent.Action<?> action = hoverEvent.action();
      HoverEventActionSerializer.INSTANCE.write(out, action);
      out.name(HOVER_EVENT_CONTENTS);
      if(action == HoverEvent.Action.SHOW_ITEM) {
        ShowItemSerializer.INSTANCE.write(out, (HoverEvent.ShowItem) hoverEvent.value());
      } else if(action == HoverEvent.Action.SHOW_ENTITY) {
        this.showEntitySerializer.write(out, (HoverEvent.ShowEntity) hoverEvent.value());
      } else if(action == HoverEvent.Action.SHOW_TEXT) {
        this.componentSerializer.write(out, (Component) hoverEvent.value());
      } else {
        throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
      }
      if(this.emitLegacyHover) {
        out.name(HOVER_EVENT_VALUE);
        this.serializeLegacyHoverEvent(hoverEvent, out);
      }

      out.endObject();
    }

    final @Nullable Key font = value.font();
    if(font != null) {
      out.name(FONT);
      KeySerializer.INSTANCE.write(out, font);
    }

    out.endObject();
  }

  private void serializeLegacyHoverEvent(final HoverEvent<?> hoverEvent, final JsonWriter out) throws IOException {
    if(hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) { // serialization is the same
      this.componentSerializer.write(out, (Component) hoverEvent.value());
    } else if(this.legacyHover != null) { // for data formats that require knowledge of SNBT
      Component serialized = null;
      try {
        if(hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
          serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity) hoverEvent.value(), this.componentSerializer::toJson);
        } else if(hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
          serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem) hoverEvent.value());
        }
      } catch(final IOException ex) {
        throw new JsonSyntaxException(ex);
      }
      if(serialized != null) {
        this.componentSerializer.write(out, serialized);
      } else {
        out.nullValue();
      }
    } else {
      out.nullValue();
    }
  }
}
