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
package net.kyori.adventure.text;

import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Component} that displays the client's current keybind for the supplied action.
 *
 * <p>This component takes:</p>
 * <dl>
 *   <dt>keybind</dt>
 *   <dd>a keybind identifier for a action. (e.g key.inventory, key.jump etc..)</dd>
 * </dl>
 *
 * @since 4.0.0
 * @sinceMinecraft 1.12
 */
public interface KeybindComponent extends BuildableComponent<KeybindComponent, KeybindComponent.Builder>, ScopedComponent<KeybindComponent> {
  /**
   * Gets the keybind.
   *
   * @return the keybind
   * @since 4.0.0
   */
  @NotNull String keybind();

  /**
   * Sets the keybind.
   *
   * @param keybind the keybind
   * @return a copy of this component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull KeybindComponent keybind(final @NotNull String keybind);

  /**
   * Sets the keybind.
   *
   * @param keybind the keybind
   * @return a copy of this component
   * @since 4.9.0
   */
  @Contract(pure = true)
  default @NotNull KeybindComponent keybind(final @NotNull KeybindLike keybind) {
    return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
  }

  /**
   * Something that can provide a keybind identifier.
   *
   * @since 4.9.0
   */
  interface KeybindLike {
    /**
     * Gets the keybind identifier.
     *
     * @return the keybind identifier
     * @since 4.9.0
     */
    @NotNull String asKeybind();
  }

  /**
   * A keybind component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<KeybindComponent, Builder> {
    /**
     * Sets the keybind.
     *
     * @param keybind the keybind
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder keybind(final @NotNull String keybind);

    /**
     * Sets the keybind.
     *
     * @param keybind the keybind
     * @return this builder
     * @since 4.9.0
     */
    @Contract(pure = true)
    default @NotNull Builder keybind(final @NotNull KeybindLike keybind) {
      return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
    }
  }
}
