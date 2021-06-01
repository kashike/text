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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * A serializer that will output to Configurate {@link ConfigurationNode}s.
 *
 * <p>This serializer only modifies its own serializer collection. To add to another collection,
 * get
 * that collection. Serializers are added for every Adventure data type.</p>
 *
 * <p>The preferred way to use Configurate integration is by registering this serializer's
 * {@linkplain TypeSerializer type serializers} with a separate </p>
 *
 * @since 4.2.0
 */
public interface ConfigurateComponentSerializer extends ComponentSerializer<Component, Component, ConfigurationNode> {
  /**
   * Get an instance with default settings.
   *
   * @return the shared default instance
   * @since 4.2.0
   */
  static @NonNull ConfigurateComponentSerializer configurate() {
    return ConfigurateComponentSerializerImpl.INSTANCE;
  }

  /**
   * Create a new builder for a customized Configurate serializer.
   *
   * @return a new builder
   * @since 4.2.0
   */
  static @NonNull Builder builder() {
    return new ConfigurateComponentSerializerImpl.Builder();
  }

  /**
   * Get the serializers provided for Adventure.
   *
   * @return a collection containing Adventure serializers
   * @since 4.2.0
   */
  @NonNull TypeSerializerCollection serializers();

  /**
   * A builder for a configurate serializer instance.
   *
   * @since 4.2.0
   */
  interface Builder {
    /**
     * Set the serializer to use when reading Components.
     *
     * <p>While the created serializer will always be able to read and write {@linkplain Component Components}
     * in their object structure, for configuration purposes it is often easier to work with Components as Strings
     * using one of a variety of available representations.</p>
     *
     * @param stringSerializer string serializer to use
     * @return this builder
     * @since 4.2.0
     */
    @NonNull Builder scalarSerializer(final @NonNull ComponentSerializer<Component, ?, String> stringSerializer);

    /**
     * If the {@link #scalarSerializer(ComponentSerializer)} is set, output components as serialized strings
     * rather than following an object structure.
     *
     * <p>By default, Components are serialized in object form, and deserialized in either format
     * based on the configured {@link #scalarSerializer(ComponentSerializer)}.</p>
     *
     * @param stringComponents Whether to output as strings
     * @return this builder
     * @since 4.2.0
     */
    @NonNull Builder outputStringComponents(final boolean stringComponents);

    /**
     * Create a new component serializer instance.
     *
     * @return new serializer
     * @since 4.2.0
     */
    @NonNull ConfigurateComponentSerializer build();
  }
}
