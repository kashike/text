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
package net.kyori.adventure.text.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.util.HSVLike;
import net.kyori.adventure.util.Index;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * The named text colours in Minecraft: Java Edition.
 *
 * @since 4.0.0
 */
public final class NamedTextColor implements TextColor {
  private static final int BLACK_VALUE = 0x000000;
  private static final int DARK_BLUE_VALUE = 0x0000aa;
  private static final int DARK_GREEN_VALUE = 0x00aa00;
  private static final int DARK_AQUA_VALUE = 0x00aaaa;
  private static final int DARK_RED_VALUE = 0xaa0000;
  private static final int DARK_PURPLE_VALUE = 0xaa00aa;
  private static final int GOLD_VALUE = 0xffaa00;
  private static final int GRAY_VALUE = 0xaaaaaa;
  private static final int DARK_GRAY_VALUE = 0x555555;
  private static final int BLUE_VALUE = 0x5555ff;
  private static final int GREEN_VALUE = 0x55ff55;
  private static final int AQUA_VALUE = 0x55ffff;
  private static final int RED_VALUE = 0xff5555;
  private static final int LIGHT_PURPLE_VALUE = 0xff55ff;
  private static final int YELLOW_VALUE = 0xffff55;
  private static final int WHITE_VALUE = 0xffffff;

  /**
   * The standard {@code black} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor BLACK = new NamedTextColor("black", BLACK_VALUE);
  /**
   * The standard {@code dark_blue} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_BLUE = new NamedTextColor("dark_blue", DARK_BLUE_VALUE);
  /**
   * The standard {@code dark_green} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_GREEN = new NamedTextColor("dark_green", DARK_GREEN_VALUE);
  /**
   * The standard {@code dark_aqua} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_AQUA = new NamedTextColor("dark_aqua", DARK_AQUA_VALUE);
  /**
   * The standard {@code dark_red} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_RED = new NamedTextColor("dark_red", DARK_RED_VALUE);
  /**
   * The standard {@code dark_purple} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_PURPLE = new NamedTextColor("dark_purple", DARK_PURPLE_VALUE);
  /**
   * The standard {@code gold} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor GOLD = new NamedTextColor("gold", GOLD_VALUE);
  /**
   * The standard {@code gray} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor GRAY = new NamedTextColor("gray", GRAY_VALUE);
  /**
   * The standard {@code dark_gray} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor DARK_GRAY = new NamedTextColor("dark_gray", DARK_GRAY_VALUE);
  /**
   * The standard {@code blue} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor BLUE = new NamedTextColor("blue", BLUE_VALUE);
  /**
   * The standard {@code green} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor GREEN = new NamedTextColor("green", GREEN_VALUE);
  /**
   * The standard {@code aqua} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor AQUA = new NamedTextColor("aqua", AQUA_VALUE);
  /**
   * The standard {@code red} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor RED = new NamedTextColor("red", RED_VALUE);
  /**
   * The standard {@code light_purple} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor LIGHT_PURPLE = new NamedTextColor("light_purple", LIGHT_PURPLE_VALUE);
  /**
   * The standard {@code yellow} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor YELLOW = new NamedTextColor("yellow", YELLOW_VALUE);
  /**
   * The standard {@code white} colour.
   *
   * @since 4.0.0
   */
  public static final NamedTextColor WHITE = new NamedTextColor("white", WHITE_VALUE);

  private static final List<NamedTextColor> VALUES = Collections.unmodifiableList(Arrays.asList(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE));
  /**
   * An index of name to color.
   *
   * @since 4.0.0
   */
  public static final Index<String, NamedTextColor> NAMES = Index.create(constant -> constant.name, VALUES);

  /**
   * Gets the named color exactly matching the provided color.
   *
   * @param value the color to match
   * @return the matched color, or null
   * @since 4.0.0
   */
  public static @Nullable NamedTextColor ofExact(final int value) {
    if(value == BLACK_VALUE) return BLACK;
    else if(value == DARK_BLUE_VALUE) return DARK_BLUE;
    else if(value == DARK_GREEN_VALUE) return DARK_GREEN;
    else if(value == DARK_AQUA_VALUE) return DARK_AQUA;
    else if(value == DARK_RED_VALUE) return DARK_RED;
    else if(value == DARK_PURPLE_VALUE) return DARK_PURPLE;
    else if(value == GOLD_VALUE) return GOLD;
    else if(value == GRAY_VALUE) return GRAY;
    else if(value == DARK_GRAY_VALUE) return DARK_GRAY;
    else if(value == BLUE_VALUE) return BLUE;
    else if(value == GREEN_VALUE) return GREEN;
    else if(value == AQUA_VALUE) return AQUA;
    else if(value == RED_VALUE) return RED;
    else if(value == LIGHT_PURPLE_VALUE) return LIGHT_PURPLE;
    else if(value == YELLOW_VALUE) return YELLOW;
    else if(value == WHITE_VALUE) return WHITE;
    return null;
  }

  /**
   * Find the named colour nearest to the provided colour.
   *
   * @param any colour to match
   * @return nearest named colour. will always return a value
   * @since 4.0.0
   */
  public static @NonNull NamedTextColor nearestTo(final @NonNull TextColor any) {
    if(any instanceof NamedTextColor) {
      return (NamedTextColor) any;
    }

    requireNonNull(any, "color");

    float matchedDistance = Float.MAX_VALUE;
    NamedTextColor match = VALUES.get(0);
    for(int i = 0, length = VALUES.size(); i < length; i++) {
      final NamedTextColor potential = VALUES.get(i);
      final float distance = distance(any.asHSV(), potential.asHSV());
      if(distance < matchedDistance) {
        match = potential;
        matchedDistance = distance;
      }
      if(distance == 0) {
        break; // same colour! whoo!
      }
    }
    return match;
  }

  /**
   * Returns a distance metric to the other colour.
   *
   * <p>This value is unitless and should only be used to compare with other text colours.</p>
   *
   * @param other colour to compare to
   * @return distance metric
   */
  private static float distance(final @NonNull HSVLike self, final @NonNull HSVLike other) {
    // weight hue more heavily than saturation and brightness. kind of magic numbers, but is fine for our use case of downsampling to a set of colors
    final float hueDistance = 3 * Math.min(Math.abs(self.h() - other.h()), 1f - Math.abs(self.h() - other.h()));
    final float saturationDiff = self.s() - other.s();
    final float valueDiff = self.v() - other.v();
    return hueDistance * hueDistance + saturationDiff * saturationDiff + valueDiff * valueDiff;
  }

  private final String name;
  private final int value;
  private final HSVLike hsv;

  private NamedTextColor(final String name, final int value) {
    this.name = name;
    this.value = value;
    this.hsv = HSVLike.fromRGB(this.red(), this.green(), this.blue());
  }

  @Override
  public int value() {
    return this.value;
  }

  @Override
  public @NonNull HSVLike asHSV() {
    return this.hsv;
  }

  @Override
  public @NonNull String toString() {
    return this.name;
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(ExaminableProperty.of("name", this.name)),
      TextColor.super.examinableProperties()
    );
  }
}
