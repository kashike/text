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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.util.Buildable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * An abstract implementation of a text component.
 *
 * @since 4.0.0
 */
@Debug.Renderer(text = "this.debuggerString()", childrenArray = "this.children().toArray()", hasChildren = "!this.children().isEmpty()")
public abstract class AbstractComponent implements Component {
  private static final Predicate<Component> NOT_EMPTY = component -> component != Component.empty();

  /**
   * The list of children.
   */
  protected final List<Component> children;
  /**
   * The style of this component.
   */
  protected final Style style;

  protected AbstractComponent(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style) {
    this.children = ComponentLike.asComponents(children, NOT_EMPTY);
    this.style = style;
  }

  @Override
  public final @NotNull List<Component> children() {
    return this.children;
  }

  @Override
  public final @NotNull Style style() {
    return this.style;
  }

  @Override
  public @NotNull Component replaceText(final @NotNull Consumer<TextReplacementConfig.Builder> configurer) {
    requireNonNull(configurer, "configurer");
    return this.replaceText(Buildable.configureAndBuild(TextReplacementConfig.builder(), configurer));
  }

  @Override
  public @NotNull Component replaceText(final @NotNull TextReplacementConfig config) {
    requireNonNull(config, "replacement");
    if (!(config instanceof TextReplacementConfigImpl)) {
      throw new IllegalArgumentException("Provided replacement was a custom TextReplacementConfig implementation, which is not supported.");
    }
    return TextReplacementRenderer.INSTANCE.render(this, ((TextReplacementConfigImpl) config).createState());
  }

  @Override
  public @NonNull Component compact() {
    return ComponentCompacting.optimize(this, null);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof AbstractComponent)) return false;
    final AbstractComponent that = (AbstractComponent) other;
    return Objects.equals(this.children, that.children)
      && Objects.equals(this.style, that.style);
  }

  @Override
  public int hashCode() {
    int result = this.children.hashCode();
    result = (31 * result) + this.style.hashCode();
    return result;
  }

  @SuppressWarnings("unused")
  private String debuggerString() {
    return StringExaminer.simpleEscaping().examine(this.examinableName(), this.examinablePropertiesWithoutChildren());
  }

  protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
    return Stream.of(ExaminableProperty.of("style", this.style));
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      this.examinablePropertiesWithoutChildren(),
      Stream.of(
        ExaminableProperty.of("children", this.children)
      )
    );
  }

  @Override
  public String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }
}
