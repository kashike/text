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
import java.time.Duration;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class TitleSerializer implements TypeSerializer<Title> {
  static final TitleSerializer INSTANCE = new TitleSerializer();

  private TitleSerializer() {
  }

  static final Duration KEEP = Duration.ofSeconds(-1);
  static final String TITLE = "title";
  static final String SUBTITLE = "subtitle";
  static final String TIMES = "times";
  static final String FADE_IN = "fade-in";
  static final String STAY = "stay";
  static final String FADE_OUT = "fade-out";

  @Override
  public @Nullable Title deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws SerializationException {
    if(value.empty()) {
      return null;
    }
    final Component title = value.node(TITLE).get(Component.class, Component.empty());
    final Component subtitle = value.node(SUBTITLE).get(Component.class, Component.empty());

    final Duration fadeIn = value.node(TIMES, FADE_IN).get(Duration.class, KEEP);
    final Duration stay = value.node(TIMES, STAY).get(Duration.class, KEEP);
    final Duration fadeOut = value.node(TIMES, FADE_OUT).get(Duration.class, KEEP);

    if(!Objects.equals(fadeIn, KEEP) || !Objects.equals(stay, KEEP) || !Objects.equals(fadeOut, KEEP)) {
      return Title.title(title, subtitle, Title.Times.of(fadeIn, stay, fadeOut));
    } else {
      return Title.title(title, subtitle);
    }
  }

  @Override
  public void serialize(final @NonNull Type type, final @Nullable Title obj, final @NonNull ConfigurationNode value) throws SerializationException {
    if(obj == null) {
      value.set(null);
      return;
    }

    value.node(TITLE).set(Component.class, obj.title());
    value.node(SUBTITLE).set(Component.class, obj.subtitle());
    final Title.@Nullable Times times = obj.times();
    value.node(TIMES, FADE_IN).set(Duration.class, times == null || times == Title.DEFAULT_TIMES ? null : times.fadeIn());
    value.node(TIMES, STAY).set(Duration.class, times == null || times == Title.DEFAULT_TIMES ? null : times.stay());
    value.node(TIMES, FADE_OUT).set(Duration.class, times == null || times == Title.DEFAULT_TIMES ? null : times.fadeOut());
  }
}
