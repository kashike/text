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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Similar to a {@code boolean} but with three states.
 *
 * @since 4.8.0
 */
public enum TriState {
  /**
   * State describing the absence of a value.
   *
   * @since 4.8.0
   */
  NOT_SET,
  /**
   * State describing a {@code false} value.
   *
   * @since 4.8.0
   */
  FALSE,
  /**
   * State describing a {@code true} value.
   *
   * @since 4.8.0
   */
  TRUE;

  /**
   * Gets a state from a {@code boolean}.
   *
   * @param value the boolean
   * @return a tri-state
   * @since 4.8.0
   */
  public static @NonNull TriState byBoolean(final boolean value) {
    return value ? TRUE : FALSE;
  }

  /**
   * Gets a state from a {@link Boolean}.
   *
   * @param value the boolean
   * @return a tri-state
   * @since 4.8.0
   */
  public static @NonNull TriState byBoolean(final @Nullable Boolean value) {
    return value == null ? NOT_SET : byBoolean(value.booleanValue());
  }
}
