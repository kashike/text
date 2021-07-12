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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.util.Buildable;
import net.kyori.adventure.util.MonkeyBars;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A style applies visual effects or extra functionality to {@link Component}s,
 * such as {@link TextColor}s, {@link TextDecoration}s, {@link ClickEvent}s etc.
 *
 * <p>Some examples of valid styles:</p>
 *   <pre>
 *     Style myStyle = Style.style(ClickEvent.openUrl(url), NamedTextColor.RED, TextDecoration.BOLD);
 *     Style yourStyle = Style.style(TextColor.color(20, 30, 40), HoverEvent.showText(Component.text("Wow!"));
 *     Style ourStyle = Style.style().color(NamedTextColor.WHITE).build();
 *   </pre>
 *
 * <p>A note about fonts: the {@link Key} in this context represents the resource location
 * of the font in the same way as {@link Sound}s</p>
 *
 * @since 4.0.0
 */
@ApiStatus.NonExtendable
public interface Style extends Buildable<Style, Style.Builder>, Examinable {
  /**
   * The default font.
   *
   * @since 4.0.0
   * @sinceMinecraft 1.16
   */
  Key DEFAULT_FONT = Key.key("default");

  /**
   * Gets an empty style.
   *
   * @return empty style
   * @since 4.0.0
   */
  static @NotNull Style empty() {
    return StyleImpl.EMPTY;
  }

  /**
   * Creates a builder.
   *
   * @return a builder
   * @since 4.0.0
   */
  static @NotNull Builder style() {
    return new StyleImpl.BuilderImpl();
  }

  /**
   * Creates a style.
   *
   * @param consumer the builder consumer
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @NotNull Consumer<Builder> consumer) {
    return Buildable.configureAndBuild(style(), consumer);
  }

  /**
   * Creates a style with color.
   *
   * @param color the style
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @Nullable TextColor color) {
    if (color == null) return empty();
    return new StyleImpl(null, color, Stream.of(TextDecoration.values()).map(decoration -> TextDecoration.State.NOT_SET).toArray(TextDecoration.State[]::new), null, null, null);
  }

  /**
   * Creates a style with decoration.
   *
   * @param decoration the decoration
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @NotNull TextDecoration decoration) {
    return style().decoration(decoration, true).build();
  }

  /**
   * Creates a style with color and decorations.
   *
   * @param color the style
   * @param decorations the decorations
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @Nullable TextColor color, final TextDecoration@NotNull... decorations) {
    final Builder builder = style();
    builder.color(color);
    StyleImpl.decorate(builder, decorations);
    return builder.build();
  }

  /**
   * Creates a style with color and decorations.
   *
   * @param color the style
   * @param decorations the decorations
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @Nullable TextColor color, final Set<TextDecoration> decorations) {
    final Builder builder = style();
    builder.color(color);
    if (!decorations.isEmpty()) {
      for (final TextDecoration decoration : decorations) {
        builder.decoration(decoration, true);
      }
    }
    return builder.build();
  }

  /**
   * Creates a style with {@code applicables} applied.
   *
   * @param applicables the applicables
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final StyleBuilderApplicable@NotNull... applicables) {
    if (applicables.length == 0) return empty();
    final Builder builder = style();
    for (int i = 0, length = applicables.length; i < length; i++) {
      applicables[i].styleApply(builder);
    }
    return builder.build();
  }

  /**
   * Creates a style with {@code applicables} applied.
   *
   * @param applicables the applicables
   * @return a style
   * @since 4.0.0
   */
  static @NotNull Style style(final @NotNull Iterable<? extends StyleBuilderApplicable> applicables) {
    final Builder builder = style();
    for (final StyleBuilderApplicable applicable : applicables) {
      applicable.styleApply(builder);
    }
    return builder.build();
  }

  /**
   * Edits this style.
   *
   * <p>The old style will be merge into the new style before {@code consumer} is called.</p>
   *
   * @param consumer the consumer
   * @return a new style
   * @since 4.0.0
   */
  default @NotNull Style edit(final @NotNull Consumer<Builder> consumer) {
    return this.edit(consumer, Merge.Strategy.ALWAYS);
  }

  /**
   * Edits this style.
   *
   * @param consumer the consumer
   * @param strategy the merge strategy
   * @return a new style
   * @since 4.0.0
   */
  default @NotNull Style edit(final @NotNull Consumer<Builder> consumer, final Merge.@NotNull Strategy strategy) {
    return style(style -> {
      if (strategy == Merge.Strategy.ALWAYS) {
        style.merge(this, strategy);
      }
      consumer.accept(style);
      if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET) {
        style.merge(this, strategy);
      }
    });
  }

  /**
   * Gets the font.
   *
   * @return the font
   * @since 4.0.0
   * @sinceMinecraft 1.16
   */
  @Nullable Key font();

  /**
   * Sets the font.
   *
   *
   * @param font the font
   * @return a style
   * @since 4.0.0
   * @sinceMinecraft 1.16
   */
  @NotNull Style font(final @Nullable Key font);

  /**
   * Gets the color.
   *
   * @return the color
   * @since 4.0.0
   */
  @Nullable TextColor color();

  /**
   * Sets the color.
   *
   * @param color the color
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style color(final @Nullable TextColor color);

  /**
   * Sets the color if there isn't one set already.
   *
   * @param color the color
   * @return this builder
   * @since 4.0.0
   */
  @NotNull Style colorIfAbsent(final @Nullable TextColor color);

  /**
   * Tests if this style has a decoration.
   *
   * @param decoration the decoration
   * @return {@code true} if this style has the decoration, {@code false} if this
   *     style does not have the decoration
   * @since 4.0.0
   */
  default boolean hasDecoration(final @NotNull TextDecoration decoration) {
    return this.decoration(decoration) == TextDecoration.State.TRUE;
  }

  /**
   * Gets the state of a decoration on this style.
   *
   * @param decoration the decoration
   * @return {@link TextDecoration.State#TRUE} if this style has the decoration,
   *     {@link TextDecoration.State#FALSE} if this style does not have the decoration,
   *     and {@link TextDecoration.State#NOT_SET} if not set
   * @since 4.0.0
   */
  TextDecoration.@NotNull State decoration(final @NotNull TextDecoration decoration);

  /**
   * Sets the state of {@code decoration} to {@link TextDecoration.State#TRUE} on this style.
   *
   * @param decoration the decoration
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style decorate(final @NotNull TextDecoration decoration) {
    return this.decoration(decoration, TextDecoration.State.TRUE);
  }

  /**
   * Sets the state of a decoration on this style.
   *
   * @param decoration the decoration
   * @param flag {@code true} if this style should have the decoration, {@code false} if
   *     this style should not have the decoration
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style decoration(final @NotNull TextDecoration decoration, final boolean flag) {
    return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
  }

  /**
   * Sets the value of a decoration on this style.
   *
   * @param decoration the decoration
   * @param state {@link TextDecoration.State#TRUE} if this style should have the
   *     decoration, {@link TextDecoration.State#FALSE} if this style should not
   *     have the decoration, and {@link TextDecoration.State#NOT_SET} if the decoration
   *     should not have a set value
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state);

  /**
   * Gets a map of decorations this style has.
   *
   * @return a set of decorations this style has
   * @since 4.0.0
   */
  @Unmodifiable @NotNull Map<TextDecoration, TextDecoration.State> decorations();

  /**
   * Sets decorations for this style using the specified {@code decorations} map.
   *
   * <p>If a given decoration does not have a value explicitly set, the value of that particular decoration is not changed.</p>
   *
   * @param decorations the decorations
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style decorations(final @NotNull Map<TextDecoration, TextDecoration.State> decorations);

  /**
   * Gets the click event.
   *
   * @return the click event
   * @since 4.0.0
   */
  @Nullable ClickEvent clickEvent();

  /**
   * Sets the click event.
   *
   * @param event the click event
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style clickEvent(final @Nullable ClickEvent event);

  /**
   * Gets the hover event.
   *
   * @return the hover event
   * @since 4.0.0
   */
  @Nullable HoverEvent<?> hoverEvent();

  /**
   * Sets the hover event.
   *
   * @param source the hover event source
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style hoverEvent(final @Nullable HoverEventSource<?> source);

  /**
   * Gets the string to be inserted when this style is shift-clicked.
   *
   * @return the insertion string
   * @since 4.0.0
   */
  @Nullable String insertion();

  /**
   * Sets the string to be inserted when this style is shift-clicked.
   *
   * @param insertion the insertion string
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style insertion(final @Nullable String insertion);

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that) {
    return this.merge(that, Merge.all());
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param strategy the merge strategy
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy) {
    return this.merge(that, strategy, Merge.all());
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param merge the part to merge
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final @NotNull Merge merge) {
    return this.merge(that, Collections.singleton(merge));
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param strategy the merge strategy
   * @param merge the part to merge
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Merge merge) {
    return this.merge(that, strategy, Collections.singleton(merge));
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param merges the parts to merge
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final @NotNull Merge@NotNull... merges) {
    return this.merge(that, Merge.of(merges));
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param strategy the merge strategy
   * @param merges the parts to merge
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Merge@NotNull... merges) {
    return this.merge(that, strategy, Merge.of(merges));
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param merges the parts to merge
   * @return a style
   * @since 4.0.0
   */
  default @NotNull Style merge(final @NotNull Style that, final @NotNull Set<Merge> merges) {
    return this.merge(that, Merge.Strategy.ALWAYS, merges);
  }

  /**
   * Merges from another style into this style.
   *
   * @param that the other style
   * @param strategy the merge strategy
   * @param merges the parts to merge
   * @return a style
   * @since 4.0.0
   */
  @NotNull Style merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Set<Merge> merges);

  /**
   * Tests if this style is empty.
   *
   * @return {@code true} if this style is empty, {@code false} if this
   *     style is not empty
   * @since 4.0.0
   */
  boolean isEmpty();

  /**
   * Create a builder from this style.
   *
   * @return a builder
   */
  @Override
  @NotNull Builder toBuilder();

  /**
   * A merge choice.
   *
   * @since 4.0.0
   */
  enum Merge {
    /**
     * Merges {@link Style#color()}.
     *
     * @since 4.0.0
     */
    COLOR,
    /**
     * Merges {@link Style#decorations()}.
     *
     * @since 4.0.0
     */
    DECORATIONS,
    /**
     * Merges {@link Style#clickEvent()} and {@link Style#hoverEvent()}.
     *
     * @since 4.0.0
     */
    EVENTS,
    /**
     * Merges {@link Style#insertion()}.
     *
     * @since 4.0.0
     */
    INSERTION,
    /**
     * Merges {@link Style#font()}.
     *
     * @since 4.0.0
     */
    FONT;

    static final Set<Merge> ALL = of(values());
    static final Set<Merge> COLOR_AND_DECORATIONS = of(COLOR, DECORATIONS);

    /**
     * Gets a merge set of all merge types.
     *
     * @return a merge set
     * @since 4.0.0
     */
    public static @Unmodifiable @NotNull Set<Merge> all() {
      return ALL;
    }

    /**
     * Gets a merge set containing {@link #COLOR} and {@link #DECORATIONS}.
     *
     * @return a merge set
     * @since 4.0.0
     */
    public static @Unmodifiable @NotNull Set<Merge> colorAndDecorations() {
      return COLOR_AND_DECORATIONS;
    }

    /**
     * Creates a merge set.
     *
     * @param merges the merge parts
     * @return a merge set
     * @since 4.0.0
     */
    public static @Unmodifiable @NotNull Set<Merge> of(final Merge@NotNull... merges) {
      return MonkeyBars.enumSet(Merge.class, merges);
    }

    static boolean hasAll(final @NotNull Set<Merge> merges) {
      return merges.size() == ALL.size();
    }

    /**
     * A merge strategy.
     *
     * @since 4.0.0
     */
    public enum Strategy {
      /**
       * Always merge onto target.
       *
       * @since 4.0.0
       */
      ALWAYS,
      /**
       * Never merges onto target.
       *
       * @since 4.0.0
       */
      NEVER,
      /**
       * Merge onto target when not already set on target.
       *
       * @since 4.0.0
       */
      IF_ABSENT_ON_TARGET;
    }
  }

  /**
   * A style builder.
   *
   * @since 4.0.0
   */
  interface Builder extends Buildable.Builder<Style> {
    /**
     * Sets the font.
     *
     * @param font the font
     * @return this builder
     * @since 4.0.0
     * @sinceMinecraft 1.16
     */
    @Contract("_ -> this")
    @NotNull Builder font(final @Nullable Key font);

    /**
     * Sets the color.
     *
     * @param color the color
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder color(final @Nullable TextColor color);

    /**
     * Sets the color if there isn't one set already.
     *
     * @param color the color
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder colorIfAbsent(final @Nullable TextColor color);

    /**
     * Sets {@code decoration} to {@link TextDecoration.State#TRUE}.
     *
     * @param decoration the decoration
     * @return a style
     * @since 4.0.0
     */
    @Contract("_ -> this")
    default @NotNull Builder decorate(final @NotNull TextDecoration decoration) {
      return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    /**
     * Sets {@code decorations} to {@link TextDecoration.State#TRUE}.
     *
     * @param decorations the decorations
     * @return a style
     * @since 4.0.0
     */
    @Contract("_ -> this")
    default @NotNull Builder decorate(final @NotNull TextDecoration@NotNull... decorations) {
      for (int i = 0, length = decorations.length; i < length; i++) {
        this.decorate(decorations[i]);
      }
      return this;
    }

    /**
     * Sets the state of a decoration on this style.
     *
     * @param decoration the decoration
     * @param flag {@code true} if this style should have the decoration, {@code false} if
     *     this style should not have the decoration
     * @return a style
     * @since 4.0.0
     */
    @Contract("_, _ -> this")
    default @NotNull Builder decoration(final @NotNull TextDecoration decoration, final boolean flag) {
      return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    /**
     * Sets the value of a decoration.
     *
     * @param decoration the decoration
     * @param state {@link TextDecoration.State#TRUE} if this component should have the
     *     decoration, {@link TextDecoration.State#FALSE} if this component should not
     *     have the decoration, and {@link TextDecoration.State#NOT_SET} if the decoration
     *     should not have a set value
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _ -> this")
    @NotNull Builder decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state);

    /**
     * Sets the click event.
     *
     * @param event the click event
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder clickEvent(final @Nullable ClickEvent event);

    /**
     * Sets the hover event.
     *
     * @param source the hover event source
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder hoverEvent(final @Nullable HoverEventSource<?> source);

    /**
     * Sets the string to be inserted.
     *
     * @param insertion the insertion string
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder insertion(final @Nullable String insertion);

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    default @NotNull Builder merge(final @NotNull Style that) {
      return this.merge(that, Merge.all());
    }

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @param strategy the merge strategy
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _ -> this")
    default @NotNull Builder merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy) {
      return this.merge(that, strategy, Merge.all());
    }

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @param merges the parts to merge
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _ -> this")
    default @NotNull Builder merge(final @NotNull Style that, final @NotNull Merge@NotNull... merges) {
      if (merges.length == 0) return this;
      return this.merge(that, Merge.of(merges));
    }

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @param strategy the merge strategy
     * @param merges the parts to merge
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _, _ -> this")
    default @NotNull Builder merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Merge@NotNull... merges) {
      if (merges.length == 0) return this;
      return this.merge(that, strategy, Merge.of(merges));
    }

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @param merges the parts to merge
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _ -> this")
    default @NotNull Builder merge(final @NotNull Style that, final @NotNull Set<Merge> merges) {
      return this.merge(that, Merge.Strategy.ALWAYS, merges);
    }

    /**
     * Merges from another style into this style.
     *
     * @param that the other style
     * @param strategy the merge strategy
     * @param merges the parts to merge
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_, _, _ -> this")
    @NotNull Builder merge(final @NotNull Style that, final Merge.@NotNull Strategy strategy, final @NotNull Set<Merge> merges);

    /**
     * Applies {@code applicable} to this builder.
     *
     * @param applicable the applicable
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    default @NotNull Builder apply(final @NotNull StyleBuilderApplicable applicable) {
      applicable.styleApply(this);
      return this;
    }

    /**
     * Builds the style.
     *
     * @return the style
     */
    @Override
    @NotNull Style build();
  }
}
