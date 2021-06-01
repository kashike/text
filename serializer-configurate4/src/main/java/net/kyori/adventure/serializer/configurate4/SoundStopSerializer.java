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
import java.util.Collections;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class SoundStopSerializer implements TypeSerializer<SoundStop> {
  static final SoundStopSerializer INSTANCE = new SoundStopSerializer();

  static final String SOUND = "sound";
  static final String SOURCE = "source";

  private SoundStopSerializer() {
  }

  @Override
  public SoundStop deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    if(value.empty()) {
      return SoundStop.all();
    } else {
      final Key sound = value.node(SOUND).get(Key.class);
      final Sound.Source source = value.node(SOURCE).get(Sound.Source.class);
      if(sound == null) {
        return source == null ? SoundStop.all() : SoundStop.source(source);
      } else {
        return source == null ? SoundStop.named(sound) : SoundStop.namedOnSource(sound, source);
      }
    }
  }

  @Override
  public void serialize(final @NonNull Type type, final @Nullable SoundStop obj, final @NonNull ConfigurationNode value) throws SerializationException {
    value.node(SOUND).set(Key.class, obj == null ? null : obj.sound());
    value.node(SOURCE).set(Sound.Source.class, obj == null ? null : obj.source());
    if(value.empty()) {
      value.set(Collections.emptyMap());
    }
  }
}
