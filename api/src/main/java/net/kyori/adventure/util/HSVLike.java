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
package net.kyori.adventure.util;

import java.util.stream.Stream;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Something that can provide hue, saturation, and value color components.
 *
 * <p>Provided values should be in the range [0, 1].</p>
 *
 * @since 4.6.0
 */
public interface HSVLike extends Examinable {
  /**
   * Creates a new HSVLike.
   *
   * @param h hue color component
   * @param s saturation color component
   * @param v value color component
   * @return a new HSVLike
   * @since 4.6.0
   */
  static @NonNull HSVLike of(final float h, final float s, final float v) {
    return new HSVLikeImpl(h, s, v);
  }

  /**
   * Creates a new HSVLike from the given red, green, and blue color components.
   *
   * @param red red color component
   * @param green green color component
   * @param blue blue color component
   * @return a new HSVLike
   * @since 4.6.0
   */
  static @NonNull HSVLike fromRGB(@IntRange(from = 0x0, to = 0xff) final int red, @IntRange(from = 0x0, to = 0xff) final int green, @IntRange(from = 0x0, to = 0xff) final int blue) {
    final float r = red / 255.0f;
    final float g = green / 255.0f;
    final float b = blue / 255.0f;

    final float min = Math.min(r, Math.min(g, b));
    final float max = Math.max(r, Math.max(g, b)); // v
    final float delta = max - min;

    final float s;
    if(max != 0) {
      s = delta / max; // s
    } else {
      // r = g = b = 0
      s = 0;
    }
    if(s == 0) { // s = 0, h is undefined
      return new HSVLikeImpl(0, s, max);
    }

    float h;
    if(r == max) {
      h = (g - b) / delta; // between yellow & magenta
    } else if(g == max) {
      h = 2 + (b - r) / delta; // between cyan & yellow
    } else {
      h = 4 + (r - g) / delta; // between magenta & cyan
    }
    h *= 60; // degrees
    if(h < 0) {
      h += 360;
    }

    return new HSVLikeImpl(h / 360.0f, s, max);
  }

  /**
   * Gets the hue component.
   *
   * @return the hue component
   * @since 4.6.0
   */
  float h();

  /**
   * Gets the saturation component.
   *
   * @return the saturation component
   * @since 4.6.0
   */
  float s();

  /**
   * Gets the value component.
   *
   * @return the value component
   * @since 4.6.0
   */
  float v();

  @Override
  default @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("h", this.h()),
      ExaminableProperty.of("s", this.s()),
      ExaminableProperty.of("v", this.v())
    );
  }
}
