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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class StyleImpl implements Style {
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();
  static final StyleImpl EMPTY = new StyleImpl(null, null, Stream.of(DECORATIONS).map(decoration -> TextDecoration.State.NOT_SET).toArray(TextDecoration.State[]::new), null, null, null);
  private final @Nullable Key font;
  private final @Nullable TextColor color;
  private final @NotNull TextDecoration.State[] decorationStates;
  private final @Nullable ClickEvent clickEvent;
  private final @Nullable HoverEvent<?> hoverEvent;
  private final @Nullable String insertion;

  static void decorate(final Builder builder, final TextDecoration[] decorations) {
    for (int i = 0, length = decorations.length; i < length; i++) {
      final TextDecoration decoration = decorations[i];
      builder.decoration(decoration, true);
    }
  }

  StyleImpl(
    final @Nullable Key font,
    final @Nullable TextColor color,
    final @NotNull TextDecoration.State[] decorationStates,
    final @Nullable ClickEvent clickEvent,
    final @Nullable HoverEvent<?> hoverEvent,
    final @Nullable String insertion
  ) {
    this.font = font;
    this.color = color;
    this.decorationStates = decorationStates;
    this.clickEvent = clickEvent;
    this.hoverEvent = hoverEvent;
    this.insertion = insertion;
  }

  @Override
  public @Nullable Key font() {
    return this.font;
  }

  @Override
  public @NotNull Style font(final @Nullable Key font) {
    if (Objects.equals(this.font, font)) return this;
    return new StyleImpl(font, this.color, this.decorationStates, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable TextColor color() {
    return this.color;
  }

  @Override
  public @NotNull Style color(final @Nullable TextColor color) {
    if (Objects.equals(this.color, color)) return this;
    return new StyleImpl(this.font, color, this.decorationStates, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @NotNull Style colorIfAbsent(final @Nullable TextColor color) {
    if (this.color == null) {
      return this.color(color);
    }
    return this;
  }

  @Override
  public TextDecoration.@NotNull State decoration(final @NotNull TextDecoration decoration) {
    return this.decorationStates[decoration.ordinal()];
  }

  @Override
  public @NotNull Style decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state) {
    requireNonNull(state, "state");
    if (this.decoration(decoration) == state) return this;
    final TextDecoration.State[] newDecorationStates = this.decorationStates.clone();
    newDecorationStates[decoration.ordinal()] = state;
    return new StyleImpl(this.font, this.color, newDecorationStates, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @NotNull Map<TextDecoration, TextDecoration.State> decorations() {
    return Stream.of(DECORATIONS).collect(Collectors.toMap(Function.identity(), this::decoration));
  }

  @Override
  public @NotNull Style decorations(final @NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    if (decorations.entrySet().stream().allMatch(entry -> this.decoration(entry.getKey()) == entry.getValue())) return this;
    return new StyleImpl(this.font, this.color, Stream.of(DECORATIONS).map(unmappedDecoration -> decorations.getOrDefault(unmappedDecoration, this.decoration(unmappedDecoration))).toArray(TextDecoration.State[]::new), this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable ClickEvent clickEvent() {
    return this.clickEvent;
  }

  @Override
  public @NotNull Style clickEvent(final @Nullable ClickEvent event) {
    return new StyleImpl(this.font, this.color, this.decorationStates, event, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable HoverEvent<?> hoverEvent() {
    return this.hoverEvent;
  }

  @Override
  public @NotNull Style hoverEvent(final @Nullable HoverEventSource<?> source) {
    return new StyleImpl(this.font, this.color, this.decorationStates, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
  }

  @Override
  public @Nullable String insertion() {
    return this.insertion;
  }

  @Override
  public @NotNull Style insertion(final @Nullable String insertion) {
    if (Objects.equals(this.insertion, insertion)) return this;
    return new StyleImpl(this.font, this.color, this.decorationStates, this.clickEvent, this.hoverEvent, insertion);
  }

  @Override
  public @NotNull Style merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Set<Merge> merges) {
    if (that.isEmpty() || strategy == Merge.Strategy.NEVER || merges.isEmpty()) {
      // nothing to merge
      return this;
    }

    if (this.isEmpty() && Merge.hasAll(merges)) {
      // if the current style is empty and all merge types have been requested
      // we can just return the other style instead of trying to merge
      return that;
    }

    final Builder builder = this.toBuilder();
    builder.merge(that, strategy, merges);
    return builder.build();
  }

  @Override
  public boolean isEmpty() {
    return this == EMPTY;
  }

  /**
   * Create a builder from this style.
   *
   * @return a builder
   */
  @Override
  public @NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(Stream.of(DECORATIONS).map(decoration -> ExaminableProperty.of(requireNonNull(TextDecoration.NAMES.key(decoration)), this.decoration(decoration))),
      Stream.of(ExaminableProperty.of("color", this.color),
        ExaminableProperty.of("clickEvent", this.clickEvent),
        ExaminableProperty.of("hoverEvent", this.hoverEvent),
        ExaminableProperty.of("insertion", this.insertion),
        ExaminableProperty.of("font", this.font)));
  }

  @Override
  public @NotNull String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof StyleImpl)) return false;
    final StyleImpl that = (StyleImpl) other;
    return Objects.equals(this.color, that.color)
      && Arrays.equals(this.decorationStates, that.decorationStates)
      && Objects.equals(this.clickEvent, that.clickEvent)
      && Objects.equals(this.hoverEvent, that.hoverEvent)
      && Objects.equals(this.insertion, that.insertion)
      && Objects.equals(this.font, that.font);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(this.color);
    for (int i = 0, length = DECORATIONS.length; i < length; i++) {
      final TextDecoration decoration = DECORATIONS[i];
      result = (31 * result) + this.decoration(decoration).hashCode();
    }
    result = (31 * result) + Objects.hashCode(this.clickEvent);
    result = (31 * result) + Objects.hashCode(this.hoverEvent);
    result = (31 * result) + Objects.hashCode(this.insertion);
    result = (31 * result) + Objects.hashCode(this.font);
    return result;
  }

  static final class BuilderImpl implements Builder {
    @Nullable Key font;
    @Nullable TextColor color;
    @NotNull TextDecoration.State[] decorationStates;
    @Nullable ClickEvent clickEvent;
    @Nullable HoverEvent<?> hoverEvent;
    @Nullable String insertion;

    BuilderImpl() {
      this.decorationStates = StyleImpl.EMPTY.decorationStates.clone();
    }

    BuilderImpl(final @NotNull StyleImpl style) {
      this.color = style.color;
      this.decorationStates = style.decorationStates.clone();
      this.clickEvent = style.clickEvent;
      this.hoverEvent = style.hoverEvent;
      this.insertion = style.insertion;
      this.font = style.font;
    }

    @Override
    public @NotNull Builder font(final @Nullable Key font) {
      this.font = font;
      return this;
    }

    @Override
    public @NotNull Builder color(final @Nullable TextColor color) {
      this.color = color;
      return this;
    }

    @Override
    public @NotNull Builder colorIfAbsent(final @Nullable TextColor color) {
      if (this.color == null) {
        this.color = color;
      }
      return this;
    }

    @Override
    public @NotNull Builder decorate(final @NotNull TextDecoration decoration) {
      return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    @Override
    public @NotNull Builder decorate(final @NotNull TextDecoration@NotNull... decorations) {
      for (int i = 0, length = decorations.length; i < length; i++) {
        this.decorate(decorations[i]);
      }
      return this;
    }

    @Override
    public @NotNull Builder decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state) {
      requireNonNull(state, "state");
      this.decorationStates[decoration.ordinal()] = state;
      return this;
    }

    // todo(kashike): promote to public api?
    @NotNull Builder decorationIfAbsent(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state) {
      requireNonNull(state, "state");
      if (this.decorationStates[decoration.ordinal()] == TextDecoration.State.NOT_SET) {
        this.decoration(decoration, state);
      }
      return this;
    }

    @Override
    public @NotNull Builder clickEvent(final @Nullable ClickEvent event) {
      this.clickEvent = event;
      return this;
    }

    @Override
    public @NotNull Builder hoverEvent(final @Nullable HoverEventSource<?> source) {
      this.hoverEvent = HoverEventSource.unbox(source);
      return this;
    }

    @Override
    public @NotNull Builder insertion(final @Nullable String insertion) {
      this.insertion = insertion;
      return this;
    }

    @Override
    public @NotNull Builder merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Set<Merge> merges) {
      if (strategy == Merge.Strategy.NEVER || that.isEmpty() || merges.isEmpty()) {
        // nothing to merge
        return this;
      }

      final Merger merger = merger(strategy);

      if (merges.contains(Merge.COLOR)) {
        final TextColor color = that.color();
        if (color != null) merger.mergeColor(this, color);
      }

      if (merges.contains(Merge.DECORATIONS)) {
        for (int i = 0, length = DECORATIONS.length; i < length; i++) {
          final TextDecoration decoration = DECORATIONS[i];
          final TextDecoration.State state = that.decoration(decoration);
          if (state != TextDecoration.State.NOT_SET) merger.mergeDecoration(this, decoration, state);
        }
      }

      if (merges.contains(Merge.EVENTS)) {
        final ClickEvent clickEvent = that.clickEvent();
        if (clickEvent != null) merger.mergeClickEvent(this, clickEvent);

        final HoverEvent<?> hoverEvent = that.hoverEvent();
        if (hoverEvent != null) merger.mergeHoverEvent(this, hoverEvent);
      }

      if (merges.contains(Merge.INSERTION)) {
        final String insertion = that.insertion();
        if (insertion != null) merger.mergeInsertion(this, insertion);
      }

      if (merges.contains(Merge.FONT)) {
        final Key font = that.font();
        if (font != null) merger.mergeFont(this, font);
      }

      return this;
    }

    private static Merger merger(final Merge.Strategy strategy) {
      if (strategy == Merge.Strategy.ALWAYS) {
        return AlwaysMerger.INSTANCE;
      } else if (strategy == Merge.Strategy.NEVER) {
        throw new UnsupportedOperationException();
      } else if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET) {
        return IfAbsentOnTargetMerger.INSTANCE;
      }
      throw new IllegalArgumentException(strategy.name());
    }

    @Override
    public @NotNull StyleImpl build() {
      if (this.isEmpty()) {
        return EMPTY;
      }
      return new StyleImpl(this.font, this.color, this.decorationStates.clone(), this.clickEvent, this.hoverEvent, this.insertion);
    }

    private boolean isEmpty() {
      return this.color == null
        && Arrays.equals(this.decorationStates, StyleImpl.EMPTY.decorationStates)
        && this.clickEvent == null
        && this.hoverEvent == null
        && this.insertion == null
        && this.font == null;
    }
  }
}
