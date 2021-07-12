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
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * An implementation of the pixel width source which handles serialization with a set of functions
 * for resolving all components except {@link TextComponent}s.
 *
 * @param <CX> a context (player, server, locale)
 * @since 4.7.0
 */

class PixelWidthSourceImpl<CX> implements PixelWidthSource<CX> {

  static final PixelWidthSource<Object> BASIC = new BuilderImpl<Object>().build(); //Context does not matter since its never used

  private final ComponentFlattener flattener;
  private final Function<CX, CharacterWidthFunction> characterWidthFunction;

  /**
   * Get the pixel width of the given character in the Minecraft font, excluding the space between
   * characters and drop shadow. Handles alphanumerics and most common english punctuation.
   */
  static final CharacterWidthFunction DEFAULT_FONT_WIDTH = (codepoint, style) -> {
    int i;
    if(Character.isUpperCase(codepoint)) {
      i = codepoint == 'I' ? 3 : 5;
    } else if(Character.isDigit(codepoint)) {
      i = 5;
    } else if(Character.isLowerCase(codepoint)) {
      switch (codepoint) {
        case 'i':
          i = 1;
          break;

        case 'l':
          i = 2;
          break;

        case 't':
          i = 3;
          break;

        case 'f':
        case 'k':
          i = 4;
          break;

        default:
          i = 5;
          break;
      }
    } else {
      switch (codepoint) {
        case '!':
        case '.':
        case ',':
        case ';':
        case ':':
        case '|':
          i = 1;
          break;

        case '\'':
          i = 2;
          break;

        case '[':
        case ']':
        case ' ':
          i = 3;
          break;

        case '*':
        case '(':
        case ')':
        case '{':
        case '}':
        case '<':
        case '>':
          i = 4;
          break;

        case '@':
          i = 6;
          break;

        default:
          i = 5;
          break;
      }
    }

    if(style.hasDecoration(TextDecoration.BOLD)) {
      i++;
    }

    return i;
  };

  /**
   * Creates a pixel width source with a function used for getting a {@link CharacterWidthFunction}.
   *
   * <p>Any {@link CharacterWidthFunction} returned by the function should accept at least all
   * english alphanumerics and most punctuation and handle {@link TextDecoration#BOLD} in the style.
   * See {@link PixelWidthSourceImpl#DEFAULT_FONT_WIDTH} for an example.</p>
   *
   * @param characterWidthFunction a function that can provide a {@link CharacterWidthFunction} given a context
   * @since 4.5.0
   */
  PixelWidthSourceImpl(final @NonNull ComponentFlattener flattener, final @NonNull Function<CX, CharacterWidthFunction> characterWidthFunction) {
    this.flattener = flattener;
    this.characterWidthFunction = characterWidthFunction;
  }

  @Override
  public int width(final @NonNull Component component, final @NonNull CX context) {
    final AtomicInteger length = new AtomicInteger();

    this.flattener.flatten(component, new FlattenerListener() {
      final List<Style> styles = new LinkedList<>();
      Style currentStyle = Style.empty();

      @Override
      public void pushStyle(final @NonNull Style style) {
        this.styles.add(style);
        this.calculateStyle();
      }

      @Override
      public void component(final @NonNull String text) {
        length.addAndGet(PixelWidthSourceImpl.this.width(text, this.currentStyle, context));
      }

      @Override
      public void popStyle(final @NonNull Style style) {
        this.styles.remove(this.styles.size() - 1);
        this.calculateStyle();
      }

      private void calculateStyle() {
        final Style.Builder newStyle = Style.style();
        for(final Style style : this.styles) {
          newStyle.merge(style);
        }
        this.currentStyle = newStyle.build();
      }
    });

    return length.get();
  }

  @Override
  public int width(final @NonNull String string, final @NonNull Style style, final @NonNull CX context) {
    int length = 0;

    final char[] chars = string.toCharArray();
    for(int i = 0; i < chars.length; i++) {
      final char c = chars[i];

      if(Character.isLowSurrogate(c)) continue; //this character was handled on the previous iteration

      final int codepoint;
      if(Character.isHighSurrogate(c)) {
        codepoint = Character.codePointAt(chars, i);
      } else codepoint = c;

      length += this.characterWidthFunction.apply(context).widthOf(codepoint, style);
    }

    return length;
  }

  @Override
  public int width(final char c, final @NonNull Style style, final @NonNull CX context) {
    return this.characterWidthFunction.apply(context).widthOf(c, style);
  }

  @Override
  public int width(final int codepoint, final @NonNull Style style, final @NonNull CX context) {
    return this.characterWidthFunction.apply(context).widthOf(codepoint, style);
  }

  @Override
  public @NonNull Builder<CX> toBuilder() {
    return new BuilderImpl<>(this.flattener, this.characterWidthFunction);
  }

  static final class BuilderImpl<CX> implements Builder<CX> {
    private ComponentFlattener flattener;
    private Function<CX, CharacterWidthFunction> characterWidthFunction;

    BuilderImpl() {
      this.flattener = ComponentFlattener.basic();
      this.characterWidthFunction = cx -> DEFAULT_FONT_WIDTH;
    }

    BuilderImpl(final @NonNull ComponentFlattener flattener, final @NonNull Function<CX, CharacterWidthFunction> characterWidthFunction) {
      this.flattener = flattener;
      this.characterWidthFunction = characterWidthFunction;
    }

    @Override
    public @NonNull Builder<CX> flattener(final @NonNull ComponentFlattener flattener) {
      this.flattener = flattener;
      return this;
    }

    @Override
    public @NonNull Builder<CX> characterWidthFunction(final @NonNull Function<CX, CharacterWidthFunction> characterWidthFunction) {
      this.characterWidthFunction = characterWidthFunction;
      return this;
    }

    @Override
    public @NonNull PixelWidthSource<CX> build() {
      return new PixelWidthSourceImpl<>(this.flattener, this.characterWidthFunction);
    }
  }
}
