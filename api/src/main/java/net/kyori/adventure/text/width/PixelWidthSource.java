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
package net.kyori.adventure.text.width;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.translation.GlobalTranslator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.function.Function;

/**
 * A source able to return the width of text. By default accuracy can only be guaranteed for {@link TextComponent}s
 * in the standard minecraft font.
 *
 * @param <CX> a context (player, server, locale)
 * @see #addResolver(Class, ComponentResolver)
 * @since 4.5.0
 */
@ApiStatus.NonExtendable
public interface PixelWidthSource<CX> {

  /**
   * A pixel width source calculating width using the default minecraft font.
   *
   * @param <CX> the context for this pixel width source(e.g player, world, locale)
   * @param localeFunction used for automatic server-side translations using the {@link GlobalTranslator}
   * @return a pixel width source using a {@link PixelWidthSourceImpl#DEFAULT_FONT_WIDTH character function} for the default minecraft font
   * @since 4.5.0
   */
  static <CX> @NonNull PixelWidthSource<CX> defaultPixelWidth(final @NonNull Function<CX, Locale> localeFunction) {
    return new PixelWidthSourceImpl<>(cx -> PixelWidthSourceImpl.DEFAULT_FONT_WIDTH, localeFunction);
  }

  /**
   * A pixel width source calculating width using the default minecraft font.
   *
   * @param <CX> the context for this pixel width source(e.g player, world, locale)
   * @param localeFunction used for automatic server-side translations using the {@link GlobalTranslator}
   * @return a pixel width source using a custom character function
   * @since 4.5.0
   */
  static <CX> @NonNull PixelWidthSource<CX> withCustomCharacterFunction(final @NonNull Function<CX, CharacterWidthFunction> function, final @NonNull Function<CX, Locale> localeFunction) {
    return new PixelWidthSourceImpl<>(function, localeFunction);
  }

  /**
   * Calculates the pixel width of a component, given a context.
   *
   * @param component a component
   * @param context the context of this calculation
   * @return the pixel width of the component
   * @since 4.5.0
   */
  int width(final @NonNull Component component, final @NonNull CX context);

  /**
   * Calculates the pixel width of a string, given a context.
   *
   * @param string a string
   * @param style the style of the string
   * @param context the context of this calculation
   * @return the pixel width of the string
   * @since 4.5.0
   */
  int width(final @NonNull String string, final @NonNull Style style, final @NonNull CX context);

  /**
   * Calculates the pixel width of a character, given a context.
   *
   * @param character a character
   * @param style the style of the character
   * @param context the context of this calculation
   * @return the pixel width of the character
   * @since 4.5.0
   */
  int width(final char character, final @NonNull Style style, final @NonNull CX context);

  /**
   * Add a {@link ComponentResolver} to this source that can resolve a specific type of component given a context.
   * Whenever a component is resolved successfully the result is used regardless of if other resolvers are able to resolve as well.
   *
   * @param resolveFor the component type this resolver accepts
   * @param resolver the resolver
   * @see ComponentResolver
   * @since 4.5.0
   */
  <CO extends Component> void addResolver(final @NonNull Class<CO> resolveFor, final @NonNull ComponentResolver<CO, CX> resolver);

  /**
   * Something that can either resolve a {@link Component} into a {@link TextComponent}, or return {@code null} if not possible.
   *
   * @param <CX> a context (player, server, locale)
   * @since 4.5.0
   */
  interface ComponentResolver<CO extends Component, CX> {

    /**
     * Tries to display a component as plain text.
     *
     * <p>Returning a {@link TextComponent} signals that the component resolved successfully.</p>
     *
     * <p>Returning {@code null} signals that the component was not able to be resolved</p>
     *
     * @param component the component to resolve
     * @param context a context
     * @return The attempted resolve of the component
     * @since 4.5.0
     */
    @Nullable TextComponent resolve(final @NonNull CO component, final @NonNull CX context);
  }
}
