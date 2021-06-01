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

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HSVLikeTest {
  @Test
  void roundTripRgbHsvTest() {
    NamedTextColor.NAMES.values().forEach(namedTextColor -> {
      final TextColor roundTripped = TextColor.color(namedTextColor.asHSV());
      assertEquals(namedTextColor, roundTripped);
    });
  }

  @Test
  void compareRgbToHsvConversionToJavaAwtColor() {
    NamedTextColor.NAMES.values().forEach(HSVLikeTest::assertRgbToHsvConversionRoughlyMatchesJavaAwtColor);
  }

  private static void assertRgbToHsvConversionRoughlyMatchesJavaAwtColor(final @NonNull RGBLike rgb) {
    final HSVLike hsv = rgb.asHSV();
    assertArrayEquals(
      roundFloats(Color.RGBtoHSB(rgb.red(), rgb.green(), rgb.blue(), null)),
      roundFloats(new float[]{hsv.h(), hsv.s(), hsv.v()}),
      rgb.toString()
    );
  }

  private static float[] roundFloats(final float @NonNull [] floats) {
    final float[] result = new float[floats.length];
    for(int i = 0; i < floats.length; i++) {
      result[i] = BigDecimal.valueOf(floats[i]).setScale(7, RoundingMode.UP).floatValue();
    }
    return result;
  }
}
