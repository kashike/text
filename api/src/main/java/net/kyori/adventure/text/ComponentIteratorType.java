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

import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.ComponentIterator.HOVER_EVENT_CONSUMER;

/**
 * The iterator types.
 *
 * @see Component#iterator(ComponentIteratorType)
 * @see Component#spliterator(ComponentIteratorType)
 * @see Component#iterable(ComponentIteratorType)
 * @since 4.9.0
 */
public enum ComponentIteratorType {
  /**
   * A depth-first iteration.
   *
   * @since 4.9.0
   */
  DEPTH_FIRST((deque, component) -> {
    final List<Component> children = component.children();
    for (int i = children.size() - 1; i >= 0; i--) {
      deque.addFirst(children.get(i));
    }
  }),

  /**
   * A breadth-first iteration.
   *
   * @since 4.9.0
   */
  BREADTH_FIRST((deque, component) -> deque.addAll(component.children())),

  /**
   * A depth-first iteration that includes components from the {@link HoverEvent} class where the value is a component or the type is an entity with a name.
   *
   * @since 4.9.0
   */
  DEPTH_FIRST_WITH_HOVER(HOVER_EVENT_CONSUMER.andThen(DEPTH_FIRST.consumer)),

  /**
   * A breadth-first iteration that includes components from the {@link HoverEvent} class where the value is a component or the type is an entity with a name.
   *
   * @since 4.9.0
   */
  BREADTH_FIRST_WITH_HOVER(HOVER_EVENT_CONSUMER.andThen(BREADTH_FIRST.consumer));

  private final BiConsumer<Deque<Component>, Component> consumer;

  ComponentIteratorType(final @NonNull BiConsumer<Deque<Component>, Component> consumer) {
    this.consumer = consumer;
  }

  /**
   * Populates a deque with the children of this component, based on the iterator type.
   *
   * @param component the component
   * @param deque the deque
   * @since 4.9.0
   */
  void populate(final @NonNull Component component, final @NonNull Deque<Component> deque) {
    this.consumer.accept(deque, component);
  }
}
