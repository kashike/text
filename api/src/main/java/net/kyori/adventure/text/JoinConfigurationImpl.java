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

import java.util.Iterator;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class JoinConfigurationImpl implements JoinConfiguration {
  static final JoinConfigurationImpl NULL = new JoinConfigurationImpl();

  private final Component separator;
  private final Component lastSeparator;
  private final Component prefix;
  private final Component suffix;
  private final UnaryOperator<Component> operator;
  private final Component lastSeparatorIfSerial;

  private JoinConfigurationImpl() {
    this.separator = null;
    this.lastSeparator = null;
    this.prefix = null;
    this.suffix = null;
    this.operator = UnaryOperator.identity();
    this.lastSeparatorIfSerial = null;
  }

  private JoinConfigurationImpl(final @NotNull BuilderImpl builder) {
    this.separator = builder.separator == null ? null : builder.separator.asComponent();
    this.lastSeparator = builder.lastSeparator == null ? null : builder.lastSeparator.asComponent();
    this.prefix = builder.prefix == null ? null : builder.prefix.asComponent();
    this.suffix = builder.suffix == null ? null : builder.suffix.asComponent();
    this.operator = builder.operator;
    this.lastSeparatorIfSerial = builder.lastSeparatorIfSerial == null ? null : builder.lastSeparatorIfSerial.asComponent();
  }

  @Override
  public @Nullable Component prefix() {
    return this.prefix;
  }

  @Override
  public @Nullable Component suffix() {
    return this.suffix;
  }

  @Override
  public @Nullable Component separator() {
    return this.separator;
  }

  @Override
  public @Nullable Component lastSeparator() {
    return this.lastSeparator;
  }

  @Override
  public @NotNull UnaryOperator<Component> operator() {
    return this.operator;
  }

  @Override
  public @Nullable Component lastSeparatorIfSerial() {
    return this.lastSeparatorIfSerial;
  }

  @Override
  public JoinConfiguration.@NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("separator", this.separator),
      ExaminableProperty.of("lastSeparator", this.lastSeparator),
      ExaminableProperty.of("prefix", this.prefix),
      ExaminableProperty.of("suffix", this.suffix),
      ExaminableProperty.of("operator", this.operator)
    );
  }

  @Override
  public String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }

  @Contract(pure = true)
  static @NotNull Component join(final @NotNull JoinConfiguration config, final @NotNull Iterable<? extends ComponentLike> components) {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(components, "components");

    final Iterator<? extends ComponentLike> it = components.iterator();
    final Component prefix = config.prefix();
    final Component suffix = config.suffix();
    final UnaryOperator<Component> operator = config.operator();

    if (!it.hasNext()) {
      if (prefix == null && suffix == null) return Component.empty();

      final TextComponent.Builder builder = Component.text();
      if (prefix != null) builder.append(prefix);
      if (suffix != null) builder.append(suffix);
      return builder.build();
    }

    ComponentLike component = it.next();
    int componentsSeen = 0;

    if (!it.hasNext() && prefix == null && suffix == null) return operator.apply(component.asComponent());

    final Component separator = config.separator();
    final boolean hasSeparator = separator != null;

    final TextComponent.Builder builder = Component.text();
    if (prefix != null) builder.append(prefix);

    while (component != null) {
      builder.append(operator.apply(component.asComponent()));
      componentsSeen++;

      if (!it.hasNext()) {
        component = null;
      } else {
        component = it.next();

        if (it.hasNext()) {
          if (hasSeparator) builder.append(separator);
        } else {
          Component lastSeparator = null;

          if (componentsSeen > 1) lastSeparator = config.lastSeparatorIfSerial();
          if (lastSeparator == null) lastSeparator = config.lastSeparator();
          if (lastSeparator == null) lastSeparator = config.separator();

          if (lastSeparator != null) builder.append(lastSeparator);
        }
      }
    }

    if (suffix != null) builder.append(suffix);
    return builder.build();
  }

  static final class BuilderImpl implements JoinConfiguration.Builder {
    private ComponentLike separator;
    private ComponentLike lastSeparator;
    private ComponentLike prefix;
    private ComponentLike suffix;
    private UnaryOperator<Component> operator;
    private ComponentLike lastSeparatorIfSerial;

    BuilderImpl() {
      this(JoinConfigurationImpl.NULL);
    }

    private BuilderImpl(final @NotNull JoinConfigurationImpl joinConfig) {
      this.separator = joinConfig.separator;
      this.lastSeparator = joinConfig.lastSeparator;
      this.prefix = joinConfig.prefix;
      this.suffix = joinConfig.suffix;
      this.operator = joinConfig.operator;
      this.lastSeparatorIfSerial = joinConfig.lastSeparatorIfSerial;
    }

    @Override
    public @NotNull Builder prefix(final @Nullable ComponentLike prefix) {
      this.prefix = prefix;
      return this;
    }

    @Override
    public @NotNull Builder suffix(final @Nullable ComponentLike suffix) {
      this.suffix = suffix;
      return this;
    }

    @Override
    public @NotNull Builder separator(final @Nullable ComponentLike separator) {
      this.separator = separator;
      return this;
    }

    @Override
    public @NotNull Builder lastSeparator(final @Nullable ComponentLike lastSeparator) {
      this.lastSeparator = lastSeparator;
      return this;
    }

    @Override
    public @NotNull Builder lastSeparatorIfSerial(final @Nullable ComponentLike lastSeparatorIfSerial) {
      this.lastSeparatorIfSerial = lastSeparatorIfSerial;
      return this;
    }

    @Override
    public @NotNull Builder operator(final @NotNull UnaryOperator<Component> operator) {
      this.operator = Objects.requireNonNull(operator, "operator");
      return this;
    }

    @Override
    public @NotNull JoinConfiguration build() {
      return new JoinConfigurationImpl(this);
    }
  }
}
