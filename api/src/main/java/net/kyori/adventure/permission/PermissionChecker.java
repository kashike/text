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
package net.kyori.adventure.permission;

import java.util.function.Predicate;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.util.TriState;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Something that has permissions.
 *
 * @since 4.8.0
 */
public interface PermissionChecker extends Predicate<String> {
  /**
   * A pointer to a permission predicate.
   *
   * @since 4.8.0
   */
  Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key(Adventure.NAMESPACE, "permission"));

  /**
   * Creates a {@link PermissionChecker} that always returns {@code state}.
   *
   * @param state the state
   * @return a {@link PermissionChecker}
   * @since 4.8.0
   */
  static @NonNull PermissionChecker always(final TriState state) {
    if(state == TriState.TRUE) return PermissionCheckers.TRUE;
    if(state == TriState.FALSE) return PermissionCheckers.FALSE;
    return PermissionCheckers.NOT_SET;
  }

  /**
   * Checks if something has a permission.
   *
   * @param permission the permission
   * @return a tri-state result
   * @since 4.8.0
   */
  @NonNull TriState value(final String permission);

  @Override
  default boolean test(final String permission) {
    return this.value(permission) == TriState.TRUE;
  }
}
