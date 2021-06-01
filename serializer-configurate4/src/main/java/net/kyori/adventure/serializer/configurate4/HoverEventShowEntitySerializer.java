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
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class HoverEventShowEntitySerializer implements TypeSerializer<HoverEvent.ShowEntity> {
  static final HoverEventShowEntitySerializer INSTANCE = new HoverEventShowEntitySerializer();

  static final String ENTITY_TYPE = "type";
  static final String ID = "id";
  static final String NAME = "name";

  private HoverEventShowEntitySerializer() {
  }

  @Override
  public HoverEvent.ShowEntity deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    final Key typeId = value.node(ENTITY_TYPE).get(Key.class);
    final UUID id = value.node(ID).get(UUID.class);
    if(typeId == null || id == null) {
      throw new SerializationException("A show entity hover event needs type and id fields to be deserialized");
    }
    final @Nullable Component name = value.node(NAME).get(Component.class);

    return HoverEvent.ShowEntity.of(typeId, id, name);
  }

  @Override
  public void serialize(final @NonNull Type type, final HoverEvent.@Nullable ShowEntity obj, final @NonNull ConfigurationNode value) throws SerializationException {
    if(obj == null) {
      value.set(null);
      return;
    }

    value.node(ENTITY_TYPE).set(Key.class, obj.type());
    value.node(ID).set(UUID.class, obj.id());
    value.node(NAME).set(Component.class, obj.name());
  }
}
