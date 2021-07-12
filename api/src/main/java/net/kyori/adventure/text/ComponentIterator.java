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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

final class ComponentIterator implements Iterator<Component> {
  static final BiConsumer<Deque<Component>, Component> HOVER_EVENT_CONSUMER = (deque, component) -> {
    final HoverEvent<?> hoverEvent = component.hoverEvent();

    if (hoverEvent == null) return;

    final Object value = hoverEvent.value();

    if (value instanceof Component) {
      deque.addFirst((Component) value);
    } else if (value instanceof HoverEvent.ShowEntity) {
      final Component name = ((HoverEvent.ShowEntity) value).name();

      if (name != null) {
        deque.addFirst(name);
      }
    }
  };

  private Component component;
  private final ComponentIteratorType type;
  private final Deque<Component> deque;

  ComponentIterator(final @NotNull Component component, final @NotNull ComponentIteratorType type) {
    this.component = Objects.requireNonNull(component, "component");
    this.type = Objects.requireNonNull(type, "type");
    this.deque = new ArrayDeque<>();
  }

  @Override
  public boolean hasNext() {
    return this.component != null || !this.deque.isEmpty();
  }

  @Override
  public Component next() {
    if (this.component != null) {
      final Component next = this.component;
      this.component = null;
      this.type.populate(next, this.deque);
      return next;
    } else {
      if (this.deque.isEmpty()) throw new NoSuchElementException();
      this.component = this.deque.poll();
      return this.next();
    }
  }
}
