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
package net.kyori.adventure.title;

import java.time.Duration;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Ticks;
import net.kyori.examination.Examinable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents an in-game title, which can be displayed across the centre of the screen.
 *
 * @see Times
 * @since 4.0.0
 */
@ApiStatus.NonExtendable
public interface Title extends Examinable {
  /**
   * The default times.
   *
   * @since 4.0.0
   */
  Times DEFAULT_TIMES = Times.of(Ticks.duration(10), Ticks.duration(70), Ticks.duration(20));

  /**
   * Creates a title.
   *
   * @param title the title
   * @param subtitle the subtitle
   * @return the title
   * @since 4.0.0
   */
  static @NonNull Title title(final @NonNull Component title, final @NonNull Component subtitle) {
    return title(title, subtitle, DEFAULT_TIMES);
  }

  /**
   * Creates a title.
   *
   * @param title the title
   * @param subtitle the subtitle
   * @param times the times
   * @return the title
   * @since 4.0.0
   */
  static @NonNull Title title(final @NonNull Component title, final @NonNull Component subtitle, final @Nullable Times times) {
    return new TitleImpl(title, subtitle, times);
  }

  /**
   * Gets the title.
   *
   * @return the title
   * @since 4.0.0
   */
  @NonNull Component title();

  /**
   * Gets the subtitle.
   *
   * @return the subtitle
   * @since 4.0.0
   */
  @NonNull Component subtitle();

  /**
   * Gets the times.
   *
   * @return the times
   * @since 4.0.0
   */
  @Nullable Times times();

  /**
   * A part of a title.
   *
   * @since 4.8.0
   */
  enum Part {
    /**
     * Title.
     *
     * @see Title#title()
     * @since 4.8.0
     */
    TITLE,
    /**
     * Subtitle.
     *
     * @see Title#subtitle()
     * @since 4.8.0
     */
    SUBTITLE,
    /**
     * Times.
     *
     * @see Title#times()
     * @since 4.8.0
     */
    TIMES;

    /**
     * Gets a set of all parts.
     *
     * @return a set of parts
     * @since 4.8.0
     */
    public static @NonNull Set<Part> all() {
      return TitleImpl.ALL_PARTS;
    }
  }

  /**
   * Title times.
   *
   * @since 4.0.0
   */
  interface Times extends Examinable {
    /**
     * Creates times.
     *
     * @param fadeIn the fade-in time
     * @param stay the stay time
     * @param fadeOut the fade-out time
     * @return times
     * @since 4.0.0
     */
    static @NonNull Times of(final @NonNull Duration fadeIn, final @NonNull Duration stay, final @NonNull Duration fadeOut) {
      return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
    }

    /**
     * Gets the time the title will fade-in.
     *
     * @return the time the title will fade-in
     * @since 4.0.0
     */
    @NonNull Duration fadeIn();

    /**
     * Gets the time the title will stay.
     *
     * @return the time the title will stay
     * @since 4.0.0
     */
    @NonNull Duration stay();

    /**
     * Gets the time the title will fade-out.
     *
     * @return the time the title will fade-out
     * @since 4.0.0
     */
    @NonNull Duration fadeOut();
  }
}
