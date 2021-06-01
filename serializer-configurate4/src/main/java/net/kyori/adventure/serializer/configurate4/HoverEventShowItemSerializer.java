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

import java.lang.reflect.Type;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class HoverEventShowItemSerializer implements TypeSerializer<HoverEvent.ShowItem> {
  static final HoverEventShowItemSerializer INSTANCE = new HoverEventShowItemSerializer();

  static final String ID = "id";
  static final String COUNT = "count";
  static final String TAG = "tag";

  private HoverEventShowItemSerializer() {
  }

  @Override
  public HoverEvent.ShowItem deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    final Key id = value.node(ID).get(Key.class);
    if(id == null) {
      throw new SerializationException("An id is required to deserialize the show_item hover event");
    }
    final int count = value.node(COUNT).getInt(1);
    final String tag = value.node(TAG).getString();

    return HoverEvent.ShowItem.of(id, count, tag == null ? null : BinaryTagHolder.of(tag));
  }

  @Override
  public void serialize(final @NonNull Type type, final HoverEvent.@Nullable ShowItem obj, final @NonNull ConfigurationNode value) throws SerializationException {
    if(obj == null) {
      value.set(null);
      return;
    }

    value.node(ID).set(Key.class, obj.item());
    value.node(COUNT).set(obj.count());

    if(obj.nbt() == null) {
      value.node(TAG).set(null);
    } else {
      value.node(TAG).set(obj.nbt().string());
    }
  }
}
