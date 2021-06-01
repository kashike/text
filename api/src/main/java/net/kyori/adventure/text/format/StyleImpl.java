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

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

final class StyleImpl implements Style {
  static final StyleImpl EMPTY = new StyleImpl(null, null, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();
  private final @Nullable Key font;
  private final @Nullable TextColor color;
  private final TextDecoration.State obfuscated;
  private final TextDecoration.State bold;
  private final TextDecoration.State strikethrough;
  private final TextDecoration.State underlined;
  private final TextDecoration.State italic;
  private final @Nullable ClickEvent clickEvent;
  private final @Nullable HoverEvent<?> hoverEvent;
  private final @Nullable String insertion;

  static void decorate(final Builder builder, final TextDecoration[] decorations) {
    for(int i = 0, length = decorations.length; i < length; i++) {
      final TextDecoration decoration = decorations[i];
      builder.decoration(decoration, true);
    }
  }

  StyleImpl(
    final @Nullable Key font,
    final @Nullable TextColor color,
    final TextDecoration.State obfuscated,
    final TextDecoration.State bold,
    final TextDecoration.State strikethrough,
    final TextDecoration.State underlined,
    final TextDecoration.State italic,
    final @Nullable ClickEvent clickEvent,
    final @Nullable HoverEvent<?> hoverEvent,
    final @Nullable String insertion
  ) {
    this.font = font;
    this.color = color;
    this.obfuscated = obfuscated;
    this.bold = bold;
    this.strikethrough = strikethrough;
    this.underlined = underlined;
    this.italic = italic;
    this.clickEvent = clickEvent;
    this.hoverEvent = hoverEvent;
    this.insertion = insertion;
  }

  @Override
  public @Nullable Key font() {
    return this.font;
  }

  @Override
  public @NonNull Style font(final @Nullable Key font) {
    if(Objects.equals(this.font, font)) return this;
    return new StyleImpl(font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable TextColor color() {
    return this.color;
  }

  @Override
  public @NonNull Style color(final @Nullable TextColor color) {
    if(Objects.equals(this.color, color)) return this;
    return new StyleImpl(this.font, color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @NonNull Style colorIfAbsent(final @Nullable TextColor color) {
    if(this.color == null) {
      return this.color(color);
    }
    return this;
  }

  @Override
  public TextDecoration.@NonNull State decoration(final @NonNull TextDecoration decoration) {
    if(decoration == TextDecoration.BOLD) {
      return this.bold;
    } else if(decoration == TextDecoration.ITALIC) {
      return this.italic;
    } else if(decoration == TextDecoration.UNDERLINED) {
      return this.underlined;
    } else if(decoration == TextDecoration.STRIKETHROUGH) {
      return this.strikethrough;
    } else if(decoration == TextDecoration.OBFUSCATED) {
      return this.obfuscated;
    }
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
  }

  @Override
  public @NonNull Style decoration(final @NonNull TextDecoration decoration, final TextDecoration.@NonNull State state) {
    requireNonNull(state, "state");
    if(decoration == TextDecoration.BOLD) {
      return new StyleImpl(this.font, this.color, this.obfuscated, state, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    } else if(decoration == TextDecoration.ITALIC) {
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, state, this.clickEvent, this.hoverEvent, this.insertion);
    } else if(decoration == TextDecoration.UNDERLINED) {
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, state, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    } else if(decoration == TextDecoration.STRIKETHROUGH) {
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, state, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    } else if(decoration == TextDecoration.OBFUSCATED) {
      return new StyleImpl(this.font, this.color, state, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
  }

  @Override
  public @NonNull Map<TextDecoration, TextDecoration.State> decorations() {
    final Map<TextDecoration, TextDecoration.State> decorations = new EnumMap<>(TextDecoration.class);
    for(int i = 0, length = DECORATIONS.length; i < length; i++) {
      final TextDecoration decoration = DECORATIONS[i];
      final TextDecoration.State value = this.decoration(decoration);
      decorations.put(decoration, value);
    }
    return decorations;
  }

  @Override
  public @NonNull Style decorations(final @NonNull Map<TextDecoration, TextDecoration.State> decorations) {
    final TextDecoration.State obfuscated = decorations.getOrDefault(TextDecoration.OBFUSCATED, this.obfuscated);
    final TextDecoration.State bold = decorations.getOrDefault(TextDecoration.BOLD, this.bold);
    final TextDecoration.State strikethrough = decorations.getOrDefault(TextDecoration.STRIKETHROUGH, this.strikethrough);
    final TextDecoration.State underlined = decorations.getOrDefault(TextDecoration.UNDERLINED, this.underlined);
    final TextDecoration.State italic = decorations.getOrDefault(TextDecoration.ITALIC, this.italic);
    return new StyleImpl(this.font, this.color, obfuscated, bold, strikethrough, underlined, italic, this.clickEvent, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable ClickEvent clickEvent() {
    return this.clickEvent;
  }

  @Override
  public @NonNull Style clickEvent(final @Nullable ClickEvent event) {
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, event, this.hoverEvent, this.insertion);
  }

  @Override
  public @Nullable HoverEvent<?> hoverEvent() {
    return this.hoverEvent;
  }

  @Override
  public @NonNull Style hoverEvent(final @Nullable HoverEventSource<?> source) {
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
  }

  @Override
  public @Nullable String insertion() {
    return this.insertion;
  }

  @Override
  public @NonNull Style insertion(final @Nullable String insertion) {
    if(Objects.equals(this.insertion, insertion)) return this;
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, insertion);
  }

  @Override
  public @NonNull Style merge(final @NonNull Style that, final Merge.@NonNull Strategy strategy, final @NonNull Set<Merge> merges) {
    if(that.isEmpty() || strategy == Merge.Strategy.NEVER || merges.isEmpty()) {
      // nothing to merge
      return this;
    }

    if(this.isEmpty() && Merge.hasAll(merges)) {
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
  public @NonNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("color", this.color),
      ExaminableProperty.of("obfuscated", this.obfuscated),
      ExaminableProperty.of("bold", this.bold),
      ExaminableProperty.of("strikethrough", this.strikethrough),
      ExaminableProperty.of("underlined", this.underlined),
      ExaminableProperty.of("italic", this.italic),
      ExaminableProperty.of("clickEvent", this.clickEvent),
      ExaminableProperty.of("hoverEvent", this.hoverEvent),
      ExaminableProperty.of("insertion", this.insertion),
      ExaminableProperty.of("font", this.font)
    );
  }

  @Override
  public @NonNull String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof StyleImpl)) return false;
    final StyleImpl that = (StyleImpl) other;
    return Objects.equals(this.color, that.color)
      && this.obfuscated == that.obfuscated
      && this.bold == that.bold
      && this.strikethrough == that.strikethrough
      && this.underlined == that.underlined
      && this.italic == that.italic
      && Objects.equals(this.clickEvent, that.clickEvent)
      && Objects.equals(this.hoverEvent, that.hoverEvent)
      && Objects.equals(this.insertion, that.insertion)
      && Objects.equals(this.font, that.font);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(this.color);
    result = (31 * result) + this.obfuscated.hashCode();
    result = (31 * result) + this.bold.hashCode();
    result = (31 * result) + this.strikethrough.hashCode();
    result = (31 * result) + this.underlined.hashCode();
    result = (31 * result) + this.italic.hashCode();
    result = (31 * result) + Objects.hashCode(this.clickEvent);
    result = (31 * result) + Objects.hashCode(this.hoverEvent);
    result = (31 * result) + Objects.hashCode(this.insertion);
    result = (31 * result) + Objects.hashCode(this.font);
    return result;
  }

  static final class BuilderImpl implements Builder {
    @Nullable Key font;
    @Nullable TextColor color;
    TextDecoration.State obfuscated = TextDecoration.State.NOT_SET;
    TextDecoration.State bold = TextDecoration.State.NOT_SET;
    TextDecoration.State strikethrough = TextDecoration.State.NOT_SET;
    TextDecoration.State underlined = TextDecoration.State.NOT_SET;
    TextDecoration.State italic = TextDecoration.State.NOT_SET;
    @Nullable ClickEvent clickEvent;
    @Nullable HoverEvent<?> hoverEvent;
    @Nullable String insertion;

    BuilderImpl() {
    }

    BuilderImpl(final @NonNull StyleImpl style) {
      this.color = style.color;
      this.obfuscated = style.obfuscated;
      this.bold = style.bold;
      this.strikethrough = style.strikethrough;
      this.underlined = style.underlined;
      this.italic = style.italic;
      this.clickEvent = style.clickEvent;
      this.hoverEvent = style.hoverEvent;
      this.insertion = style.insertion;
      this.font = style.font;
    }

    @Override
    public @NonNull Builder font(final @Nullable Key font) {
      this.font = font;
      return this;
    }

    @Override
    public @NonNull Builder color(final @Nullable TextColor color) {
      this.color = color;
      return this;
    }

    @Override
    public @NonNull Builder colorIfAbsent(final @Nullable TextColor color) {
      if(this.color == null) {
        this.color = color;
      }
      return this;
    }

    @Override
    public @NonNull Builder decorate(final @NonNull TextDecoration decoration) {
      return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    @Override
    public @NonNull Builder decorate(final @NonNull TextDecoration@NonNull... decorations) {
      for(int i = 0, length = decorations.length; i < length; i++) {
        this.decorate(decorations[i]);
      }
      return this;
    }

    @Override
    public @NonNull Builder decoration(final @NonNull TextDecoration decoration, final TextDecoration.@NonNull State state) {
      requireNonNull(state, "state");
      if(decoration == TextDecoration.BOLD) {
        this.bold = state;
        return this;
      } else if(decoration == TextDecoration.ITALIC) {
        this.italic = state;
        return this;
      } else if(decoration == TextDecoration.UNDERLINED) {
        this.underlined = state;
        return this;
      } else if(decoration == TextDecoration.STRIKETHROUGH) {
        this.strikethrough = state;
        return this;
      } else if(decoration == TextDecoration.OBFUSCATED) {
        this.obfuscated = state;
        return this;
      }
      throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    @NonNull Builder decorationIfAbsent(final @NonNull TextDecoration decoration, final TextDecoration.@NonNull State state) {
      requireNonNull(state, "state");
      if(decoration == TextDecoration.BOLD && this.bold == TextDecoration.State.NOT_SET) {
        this.bold = state;
        return this;
      } else if(decoration == TextDecoration.ITALIC && this.italic == TextDecoration.State.NOT_SET) {
        this.italic = state;
        return this;
      } else if(decoration == TextDecoration.UNDERLINED && this.underlined == TextDecoration.State.NOT_SET) {
        this.underlined = state;
        return this;
      } else if(decoration == TextDecoration.STRIKETHROUGH && this.strikethrough == TextDecoration.State.NOT_SET) {
        this.strikethrough = state;
        return this;
      } else if(decoration == TextDecoration.OBFUSCATED && this.obfuscated == TextDecoration.State.NOT_SET) {
        this.obfuscated = state;
        return this;
      }
      throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    @Override
    public @NonNull Builder clickEvent(final @Nullable ClickEvent event) {
      this.clickEvent = event;
      return this;
    }

    @Override
    public @NonNull Builder hoverEvent(final @Nullable HoverEventSource<?> source) {
      this.hoverEvent = HoverEventSource.unbox(source);
      return this;
    }

    @Override
    public @NonNull Builder insertion(final @Nullable String insertion) {
      this.insertion = insertion;
      return this;
    }

    @Override
    public @NonNull Builder merge(final @NonNull Style that, final Merge.@NonNull Strategy strategy, final @NonNull Set<Merge> merges) {
      if(strategy == Merge.Strategy.NEVER || that.isEmpty() || merges.isEmpty()) {
        // nothing to merge
        return this;
      }

      final Merger merger = merger(strategy);

      if(merges.contains(Merge.COLOR)) {
        final TextColor color = that.color();
        if(color != null) merger.mergeColor(this, color);
      }

      if(merges.contains(Merge.DECORATIONS)) {
        for(int i = 0, length = DECORATIONS.length; i < length; i++) {
          final TextDecoration decoration = DECORATIONS[i];
          final TextDecoration.State state = that.decoration(decoration);
          if(state != TextDecoration.State.NOT_SET) merger.mergeDecoration(this, decoration, state);
        }
      }

      if(merges.contains(Merge.EVENTS)) {
        final ClickEvent clickEvent = that.clickEvent();
        if(clickEvent != null) merger.mergeClickEvent(this, clickEvent);

        final HoverEvent<?> hoverEvent = that.hoverEvent();
        if(hoverEvent != null) merger.mergeHoverEvent(this, hoverEvent);
      }

      if(merges.contains(Merge.INSERTION)) {
        final String insertion = that.insertion();
        if(insertion != null) merger.mergeInsertion(this, insertion);
      }

      if(merges.contains(Merge.FONT)) {
        final Key font = that.font();
        if(font != null) merger.mergeFont(this, font);
      }

      return this;
    }

    private static Merger merger(final Merge.Strategy strategy) {
      if(strategy == Merge.Strategy.ALWAYS) {
        return AlwaysMerger.INSTANCE;
      } else if(strategy == Merge.Strategy.NEVER) {
        throw new UnsupportedOperationException();
      } else if(strategy == Merge.Strategy.IF_ABSENT_ON_TARGET) {
        return IfAbsentOnTargetMerger.INSTANCE;
      }
      throw new IllegalArgumentException(strategy.name());
    }

    @Override
    public @NonNull StyleImpl build() {
      if(this.isEmpty()) {
        return EMPTY;
      }
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }

    private boolean isEmpty() {
      return this.color == null
        && this.obfuscated == TextDecoration.State.NOT_SET
        && this.bold == TextDecoration.State.NOT_SET
        && this.strikethrough == TextDecoration.State.NOT_SET
        && this.underlined == TextDecoration.State.NOT_SET
        && this.italic == TextDecoration.State.NOT_SET
        && this.clickEvent == null
        && this.hoverEvent == null
        && this.insertion == null
        && this.font == null;
    }
  }
}
