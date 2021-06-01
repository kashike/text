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
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.UnmodifiableCollections;

import static java.util.Objects.requireNonNull;

final class ConfigurateComponentSerializerImpl implements ConfigurateComponentSerializer {
  static final ConfigurateComponentSerializer INSTANCE = new Builder().build();
  private final TypeSerializerCollection serializers;
  private final ConfigurationOptions ownNodeOptions;
  private final @Nullable ComponentSerializer<Component, ?, String> stringSerializer;
  private final boolean serializeStringComponents;

  private ConfigurateComponentSerializerImpl(final @NonNull Builder builder) {
    this.stringSerializer = builder.stringSerializer;
    this.serializeStringComponents = builder.outputStringComponents;
    this.serializers = this.makeSerializers(TypeSerializerCollection.defaults().childBuilder());
    this.ownNodeOptions = ConfigurationOptions.defaults()
      .serializers(this.serializers)
      .nativeTypes(UnmodifiableCollections.toSet(String.class, Integer.class, Boolean.class, Double.class, Float.class));
  }

  @Override
  public @NonNull Component deserialize(final @NonNull ConfigurationNode input) {
    try {
      final @Nullable Component deserialized = input.get(Component.class);
      if(deserialized != null) {
        return deserialized;
      }
    } catch(final SerializationException e) {
      throw new IllegalArgumentException(e);
    }
    throw new IllegalArgumentException("No value present");
  }

  @Override
  public @NonNull ConfigurationNode serialize(final @NonNull Component component) {
    final ConfigurationNode base = BasicConfigurationNode.root(this.ownNodeOptions);
    try {
      base.set(Component.class, component);
    } catch(final SerializationException e) {
      throw new IllegalStateException("Unable to serialize component " + component, e);
    }
    return base;
  }

  private @NonNull TypeSerializerCollection makeSerializers(final TypeSerializerCollection.@NonNull Builder serializers) {
    return serializers
      .register(Book.class, BookTypeSerializer.INSTANCE)
      .register(Title.class, TitleSerializer.INSTANCE)
      .register(Sound.class, SoundSerializer.INSTANCE)
      .register(SoundStop.class, SoundStopSerializer.INSTANCE)
      .register(Component.class, new ComponentTypeSerializer(this.stringSerializer, this.serializeStringComponents))

      // shared types
      .register(KeySerializer.INSTANCE)
      .register(DurationSerializer.INSTANCE)
      .register(Style.class, StyleSerializer.INSTANCE)
      .register(TextColorSerializer.INSTANCE)
      .register(BlockNBTPosSerializer.INSTANCE)
      .registerExact(new IndexSerializer<>(TypeToken.get(ClickEvent.Action.class), ClickEvent.Action.NAMES))
      .registerExact(new IndexSerializer<>(new TypeToken<HoverEvent.Action<?>>() {}, HoverEvent.Action.NAMES))
      .registerExact(new IndexSerializer<>(TypeToken.get(Sound.Source.class), Sound.Source.NAMES))
      .registerExact(new IndexSerializer<>(TypeToken.get(TextDecoration.class), TextDecoration.NAMES))
      .registerExact(HoverEvent.ShowEntity.class, HoverEventShowEntitySerializer.INSTANCE)
      .registerExact(HoverEvent.ShowItem.class, HoverEventShowItemSerializer.INSTANCE)
      .build();
  }

  @Override
  public @NonNull TypeSerializerCollection serializers() {
    return this.serializers;
  }

  static class Builder implements ConfigurateComponentSerializer.Builder {
    private @Nullable ComponentSerializer<Component, ?, String> stringSerializer;
    private boolean outputStringComponents = false;

    Builder() {
    }

    @Override
    public ConfigurateComponentSerializer.@NonNull Builder scalarSerializer(final @NonNull ComponentSerializer<Component, ?, String> stringSerializer) {
      this.stringSerializer = requireNonNull(stringSerializer, "stringSerializer");
      return this;
    }

    @Override
    public ConfigurateComponentSerializer.@NonNull Builder outputStringComponents(final boolean stringComponents) {
      this.outputStringComponents = stringComponents;
      return this;
    }

    @Override
    public @NonNull ConfigurateComponentSerializer build() {
      return new ConfigurateComponentSerializerImpl(this);
    }
  }
}
