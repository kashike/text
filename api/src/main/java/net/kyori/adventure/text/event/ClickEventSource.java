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
package net.kyori.adventure.text.event;

import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Something that provides a {@link ClickEvent}.
 *
 * @since 4.9.0
 */
public interface ClickEventSource {
  /**
   * Fetches a {@link ClickEvent} from a {@code ClickEventSource}.
   *
   * @param source the click event source
   * @return a click event, or {@code null}
   * @since 4.9.0
   */
  static @Nullable ClickEvent unbox(final @Nullable ClickEventSource source) {
    return source != null ? source.asClickEvent() : null;
  }

  /**
   * Represent this object as a click event.
   *
   * @return a click event
   * @since 4.9.0
   */
  default @NotNull ClickEvent asClickEvent() {
    return this.asClickEvent(UnaryOperator.identity());
  }

  /**
   * Creates a click event derived from this object with a specific action.
   *
   * @param action the click event action
   * @return a click event
   * @since 4.9.0
   */
  default @NotNull ClickEvent asClickEvent(final ClickEvent.@NotNull Action action) {
    return this.asClickEvent(clickEvent -> clickEvent.action(action));
  }

  /**
   * Creates a click event derived from this object.
   *
   * <p>The click event will be passed through the provided callback to allow
   * transforming the event.</p>
   *
   * @param op transformation on value
   * @return a click event
   * @since 4.9.0
   */
  @NotNull ClickEvent asClickEvent(final @NotNull UnaryOperator<ClickEvent> op);
}
