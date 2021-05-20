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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SerializerFactory implements TypeAdapterFactory {
  static final Class<Key> KEY_TYPE = Key.class;
  static final Class<Component> COMPONENT_TYPE = Component.class;
  static final Class<Style> STYLE_TYPE = Style.class;
  static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
  static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
  static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
  static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
  static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
  static final Class<TextColor> COLOR_TYPE = TextColor.class;
  static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
  static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;

  private final boolean downsampleColors;
  private final LegacyHoverEventSerializer legacyHoverSerializer;
  private final boolean emitLegacyHover;

  SerializerFactory(final boolean downsampleColors, final @Nullable LegacyHoverEventSerializer legacyHoverSerializer, final boolean emitLegacyHover) {
    this.downsampleColors = downsampleColors;
    this.legacyHoverSerializer = legacyHoverSerializer;
    this.emitLegacyHover = emitLegacyHover;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
    final Class<? super T> rawType = type.getRawType();
    if(KEY_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) KeySerializer.INSTANCE;
    } else if(STYLE_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) new StyleSerializer(this.legacyHoverSerializer, this.emitLegacyHover, gson::getAdapter);
    } else if(CLICK_ACTION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ClickEventActionSerializer.INSTANCE;
    } else if(HOVER_ACTION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) HoverEventActionSerializer.INSTANCE;
    } else if(SHOW_ITEM_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ShowItemSerializer.INSTANCE;
    } else if(SHOW_ENTITY_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) new ShowEntitySerializer(gson::getAdapter);
    } else if(COLOR_WRAPPER_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) TextColorWrapper.Serializer.INSTANCE;
    } else if(COLOR_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) (this.downsampleColors ? TextColorSerializer.DOWNSAMPLE_COLOR : TextColorSerializer.INSTANCE);
    } else if(TEXT_DECORATION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) TextDecorationSerializer.INSTANCE;
    } else if(BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) BlockNBTComponentPosSerializer.INSTANCE;
    } else {
      return null;
    }
  }
}
