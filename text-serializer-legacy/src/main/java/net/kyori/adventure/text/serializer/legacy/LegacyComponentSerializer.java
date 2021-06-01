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
package net.kyori.adventure.text.serializer.legacy;

import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.util.Buildable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * A legacy component serializer.
 *
 * <p>Legacy does <b>not</b> support more complex features such as, but not limited
 * to, {@link ClickEvent} and {@link HoverEvent}.</p>
 *
 * @since 4.0.0
 */
public interface LegacyComponentSerializer extends ComponentSerializer<Component, TextComponent, String>, Buildable<LegacyComponentSerializer, LegacyComponentSerializer.Builder> {
  /**
   * Gets a component serializer for legacy-based serialization and deserialization. Note that this
   * serializer works exactly like vanilla Minecraft and does not detect any links. If you want to
   * detect and make URLs clickable, use {@link Builder#extractUrls()}.
   *
   * <p>The returned serializer uses the {@link #SECTION_CHAR section} character.</p>
   *
   * @return a component serializer for legacy serialization and deserialization
   * @since 4.0.0
   */
  static @NonNull LegacyComponentSerializer legacySection() {
    return LegacyComponentSerializerImpl.Instances.SECTION;
  }

  /**
   * Gets a component serializer for legacy-based serialization and deserialization. Note that this
   * serializer works exactly like vanilla Minecraft and does not detect any links. If you want to
   * detect and make URLs clickable, use {@link Builder#extractUrls()}.
   *
   * <p>The returned serializer uses the {@link #AMPERSAND_CHAR ampersand} character.</p>
   *
   * @return a component serializer for legacy serialization and deserialization
   * @since 4.0.0
   */
  static @NonNull LegacyComponentSerializer legacyAmpersand() {
    return LegacyComponentSerializerImpl.Instances.AMPERSAND;
  }

  /**
   * Gets a component serializer for legacy-based serialization and deserialization. Note that this
   * serializer works exactly like vanilla Minecraft and does not detect any links. If you want to
   * detect and make URLs clickable, use {@link Builder#extractUrls()}.
   *
   * @param legacyCharacter the legacy character to use
   * @return a component serializer for legacy serialization and deserialization
   * @since 4.0.0
   */
  static @NonNull LegacyComponentSerializer legacy(final char legacyCharacter) {
    if(legacyCharacter == SECTION_CHAR) {
      return legacySection();
    } else if(legacyCharacter == AMPERSAND_CHAR) {
      return legacyAmpersand();
    }
    return builder().character(legacyCharacter).build();
  }

  /**
   * Converts a legacy character ({@code 0123456789abcdefklmnor}) to a legacy format, when possible.
   *
   * @param character the legacy character
   * @return the legacy format
   * @since 4.0.0
   */
  static @Nullable LegacyFormat parseChar(final char character) {
    return LegacyComponentSerializerImpl.legacyFormat(character);
  }

  /**
   * Creates a new {@link LegacyComponentSerializer.Builder}.
   *
   * @return the builder
   * @since 4.0.0
   */
  static @NonNull Builder builder() {
    return new LegacyComponentSerializerImpl.BuilderImpl();
  }

  /**
   * The legacy character used by Minecraft. ('§')
   *
   * @since 4.0.0
   */
  char SECTION_CHAR = '§';

  /**
   * The legacy character frequently used by configurations and commands. ('&amp;')
   *
   * @since 4.0.0
   */
  char AMPERSAND_CHAR = '&';

  /**
   * The legacy character used to prefix hex colors. ('#')
   *
   * @since 4.0.0
   */
  char HEX_CHAR = '#';

  /**
   * Deserialize a component from a legacy {@link String}.
   *
   * @param input the input
   * @return the component
   */
  @Override
  @NonNull TextComponent deserialize(final @NonNull String input);

  /**
   * Serializes a component into a legacy {@link String}.
   *
   * @param component the component
   * @return the string
   */
  @Override
  @NonNull String serialize(final @NonNull Component component);

  /**
   * A builder for {@link LegacyComponentSerializer}.
   *
   * @since 4.0.0
   */
  interface Builder extends Buildable.Builder<LegacyComponentSerializer> {
    /**
     * Sets the legacy character used by the serializer.
     *
     * @param legacyCharacter the legacy character
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder character(final char legacyCharacter);

    /**
     * Sets the legacy hex character used by the serializer.
     *
     * @param legacyHexCharacter the legacy hex character.
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder hexCharacter(final char legacyHexCharacter);

    /**
     * Sets that the serializer should extract URLs into {@link ClickEvent}s
     * when deserializing.
     *
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder extractUrls();

    /**
     * Sets that the serializer should extract URLs into {@link ClickEvent}s
     * when deserializing.
     *
     * @param pattern the url pattern
     * @return this builder
     * @since 4.2.0
     */
    @NonNull Builder extractUrls(final @NonNull Pattern pattern);

    /**
     * Sets that the serializer should extract URLs into {@link ClickEvent}s
     * when deserializing.
     *
     * @param style the style to use for extracted links
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder extractUrls(final @Nullable Style style);

    /**
     * Sets that the serializer should extract URLs into {@link ClickEvent}s
     * when deserializing.
     *
     * @param pattern the url pattern
     * @return this builder
     * @since 4.2.0
     */
    @NonNull Builder extractUrls(final @NonNull Pattern pattern, final @Nullable Style style);

    /**
     * Sets that the serializer should support hex colors.
     *
     * <p>Otherwise, hex colors are downsampled to the nearest named color.</p>
     *
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder hexColors();

    /**
     * Sets that the serializer should use the '&amp;x' repeated code format when serializing hex
     * colors. Note that messages in this format can still be deserialized, even with this option
     * disabled.
     *
     * <p>This is the format adopted by the BungeeCord (and by usage, Spigot) text API.</p>
     *
     * <p>The format is difficult to manipulate and read, and its use is not recommended. Support
     * is provided for it only to allow plugin developers to use this library alongside parts of
     * the Spigot API which expect legacy strings in this format.</p>
     *
     * <p>It is recommended to use only when absolutely necessary, and when no better alternatives
     * are available.</p>
     *
     * @return this builder
     * @since 4.0.0
     */
    @NonNull Builder useUnusualXRepeatedCharacterHexFormat();

    /**
     * Use this component flattener to convert components into plain text.
     *
     * <p>By default, this serializer will use {@link ComponentFlattener#basic()}</p>
     *
     * @param flattener the flattener to use
     * @return this builder
     * @since 4.7.0
     */
    @NonNull Builder flattener(final @NonNull ComponentFlattener flattener);

    /**
     * Builds the serializer.
     *
     * @return the built serializer
     */
    @Override
    @NonNull LegacyComponentSerializer build();
  }

  /**
   * A {@link LegacyComponentSerializer} service provider.
   *
   * @since 4.8.0
   */
  @ApiStatus.Internal
  interface Provider {
    /**
     * Provides a {@link LegacyComponentSerializer} using {@link #AMPERSAND_CHAR}.
     *
     * @return a {@link LegacyComponentSerializer}
     * @since 4.8.0
     */
    @ApiStatus.Internal
    @NonNull LegacyComponentSerializer legacyAmpersand();

    /**
     * Provides a {@link LegacyComponentSerializer} using {@link #SECTION_CHAR}.
     *
     * @return a {@link LegacyComponentSerializer}
     * @since 4.8.0
     */
    @ApiStatus.Internal
    @NonNull LegacyComponentSerializer legacySection();

    /**
     * Completes the building process of {@link Builder}.
     *
     * @return a {@link Consumer}
     * @since 4.8.0
     */
    @ApiStatus.Internal
    @NonNull Consumer<Builder> legacy();
  }
}
