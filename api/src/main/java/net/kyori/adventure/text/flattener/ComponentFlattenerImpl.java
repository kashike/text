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
package net.kyori.adventure.text.flattener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.util.MonkeyBars;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

final class ComponentFlattenerImpl implements ComponentFlattener {
  @SuppressWarnings("deprecation")
  static final ComponentFlattener BASIC = new BuilderImpl()
    .mapper(KeybindComponent.class, component -> component.keybind()) // IntelliJ is wrong here, this is fine
    .mapper(ScoreComponent.class, ScoreComponent::value) // Removed in Vanilla 1.16, but we keep it for backwards compat
    .mapper(SelectorComponent.class, SelectorComponent::pattern)
    .mapper(TextComponent.class, TextComponent::content)
    .mapper(TranslatableComponent.class, TranslatableComponent::key)
    // The Vanilla game will not print NBT components, expecting those to be resolved with sender context
    .build();
  static final ComponentFlattener TEXT_ONLY = new BuilderImpl()
    .mapper(TextComponent.class, TextComponent::content)
    .build();

  private static final int MAX_DEPTH = 512;

  private final Map<Class<?>, Function<?, String>> flatteners;
  private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
  private final ConcurrentMap<Class<?>, Handler> propagatedFlatteners = new ConcurrentHashMap<>();
  private final Function<Component, String> unknownHandler;

  ComponentFlattenerImpl(final Map<Class<?>, Function<?, String>> flatteners, final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, final @Nullable Function<Component, String> unknownHandler) {
    this.flatteners = MonkeyBars.immutableMap(flatteners);
    this.complexFlatteners = MonkeyBars.immutableMap(complexFlatteners);
    this.unknownHandler = unknownHandler;
  }

  @Override
  public void flatten(final @NonNull Component input, final @NonNull FlattenerListener listener) {
    this.flatten0(input, listener, 0);
  }

  private void flatten0(final @NonNull Component input, final @NonNull FlattenerListener listener, final int depth) {
    requireNonNull(input, "input");
    requireNonNull(listener, "listener");
    if(input == Component.empty()) return;
    if(depth > MAX_DEPTH) {
      throw new IllegalStateException("Exceeded maximum depth of " + MAX_DEPTH + " while attempting to flatten components!");
    }

    final @Nullable Handler flattener = this.flattener(input);
    final Style inputStyle = input.style();

    listener.pushStyle(inputStyle);
    try {
      if(flattener != null) {
        flattener.handle(input, listener, depth + 1);
      }

      if(!input.children().isEmpty()) {
        for(final Component child : input.children()) {
          this.flatten0(child, listener, depth + 1);
        }
      }
    } finally {
      listener.popStyle(inputStyle);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Component> @Nullable Handler flattener(final T test) {
    final Handler flattener = this.propagatedFlatteners.computeIfAbsent(test.getClass(), key -> {
      // direct flatteners (just return strings)
      final @Nullable Function<Component, String> value = (Function<Component, String>) this.flatteners.get(key);
      if(value != null) return (component, listener, depth) -> listener.component(value.apply(component));

      for(final Map.Entry<Class<?>, Function<?, String>> entry : this.flatteners.entrySet()) {
        if(entry.getKey().isAssignableFrom(key)) {
          return (component, listener, depth) -> listener.component(((Function<Component, String>) entry.getValue()).apply(component));
        }
      }

      // complex flatteners (these provide extra components)
      final @Nullable BiConsumer<Component, Consumer<Component>> complexValue = (BiConsumer<Component, Consumer<Component>>) this.complexFlatteners.get(key);
      if(complexValue != null) return (component, listener, depth) -> complexValue.accept(component, c -> this.flatten0(c, listener, depth));

      for(final Map.Entry<Class<?>, BiConsumer<?, Consumer<Component>>> entry : this.complexFlatteners.entrySet()) {
        if(entry.getKey().isAssignableFrom(key)) {
          return (component, listener, depth) -> ((BiConsumer<Component, Consumer<Component>>) entry.getValue()).accept(component, c -> this.flatten0(c, listener, depth));
        }
      }

      return Handler.NONE;
    });

    if(flattener == Handler.NONE) {
      return this.unknownHandler == null ? null : (component, listener, depth) -> this.unknownHandler.apply(component);
    } else {
      return flattener;
    }
  }

  @Override
  public ComponentFlattener.@NonNull Builder toBuilder() {
    return new BuilderImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
  }

  // A function that allows nesting other flatten operations
  @FunctionalInterface
  interface Handler {
    Handler NONE = (input, listener, depth) -> {};

    void handle(final Component input, final FlattenerListener listener, final int depth);
  }

  static final class BuilderImpl implements Builder {
    private final Map<Class<?>, Function<?, String>> flatteners;
    private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
    private @Nullable Function<Component, String> unknownHandler;

    BuilderImpl() {
      this.flatteners = new HashMap<>();
      this.complexFlatteners = new HashMap<>();
    }

    BuilderImpl(final Map<Class<?>, Function<?, String>> flatteners, final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, final @Nullable Function<Component, String> unknownHandler) {
      this.flatteners = new HashMap<>(flatteners);
      this.complexFlatteners = new HashMap<>(complexFlatteners);
      this.unknownHandler = unknownHandler;
    }

    @Override
    public @NonNull ComponentFlattener build() {
      return new ComponentFlattenerImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
    }

    @Override
    public <T extends Component> ComponentFlattener.@NonNull Builder mapper(final @NonNull Class<T> type, final @NonNull Function<T, String> converter) {
      this.validateNoneInHierarchy(requireNonNull(type, "type"));
      this.flatteners.put(
        type,
        requireNonNull(converter, "converter")
      );
      this.complexFlatteners.remove(type);
      return this;
    }

    @Override
    public <T extends Component> ComponentFlattener.@NonNull Builder complexMapper(final @NonNull Class<T> type, final @NonNull BiConsumer<T, Consumer<Component>> converter) {
      this.validateNoneInHierarchy(requireNonNull(type, "type"));
      this.complexFlatteners.put(
        type,
        requireNonNull(converter, "converter")
      );
      this.flatteners.remove(type);
      return this;
    }

    private void validateNoneInHierarchy(final Class<? extends Component> beingRegistered) {
      for(final Class<?> clazz : this.flatteners.keySet()) {
        testHierarchy(clazz, beingRegistered);
      }

      for(final Class<?> clazz : this.complexFlatteners.keySet()) {
        testHierarchy(clazz, beingRegistered);
      }
    }

    private static void testHierarchy(final Class<?> existing, final Class<?> beingRegistered) {
      if(!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing))) {
        throw new IllegalArgumentException("Conflict detected between already registered type " + existing
          + " and newly registered type " + beingRegistered + "! Types in a component flattener must not share a common hierachy!");
      }
    }

    @Override
    public ComponentFlattener.@NonNull Builder unknownMapper(final @Nullable Function<Component, String> converter) {
      this.unknownHandler = converter;
      return this;
    }
  }
}
