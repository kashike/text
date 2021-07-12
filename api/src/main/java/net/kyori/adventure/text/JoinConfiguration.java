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

import java.util.function.UnaryOperator;
import net.kyori.adventure.util.Buildable;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A configuration for how a series of components can be joined.
 *
 * <p>A join configuration consists of the following parts:</p>
 * <ul>
 *   <li>
 *    <b>a prefix</b> (optional)
 *    <p>a component to be prepended to the resulting component</p>
 *   </li>
 *  <li>
 *   <b>a separator</b> (optional)
 *   <p>a component to be placed between each component</p>
 *  </li>
 *  <li>
 *   <b>a last separator</b> (optional)
 *   <p>a component to be placed between the last two components</p>
 *  </li>
 *  <li>
 *   <b>a suffix</b> (optional)
 *   <p>a component to be appended to the resulting component</p>
 *  </li>
 *  <li>
 *   <b>an operator</b> (required, defaults to {@link UnaryOperator#identity()})
 *   <p>a unary operator to change each component that is being joined, defaults to the identity operator</p>
 *  </li>
 * </ul>
 *
 * <p>Note that the last separator only acts as an override for the normal separator.
 * This means that if you do not specify a last separator, the normal separator will be placed between the last two components.
 * To omit the final separator, but still include normal separators, use {@link Component#empty()} as the last separator.</p>
 *
 * <p>If specified, the join method can use a different last separator in the case where the amount of components
 * being joined together is more than two. This can be used to insert a serial (or Oxford) comma if needed.</p>
 *
 * @see Component#join(JoinConfiguration, Iterable)
 * @see Component#join(JoinConfiguration, ComponentLike...)
 * @since 4.9.0
 */
@ApiStatus.NonExtendable
public interface JoinConfiguration extends Buildable<JoinConfiguration, JoinConfiguration.Builder>, Examinable {
  /**
   * Creates a new builder.
   *
   * @return a new builder
   * @since 4.9.0
   */
  static @NotNull Builder builder() {
    return new JoinConfigurationImpl.BuilderImpl();
  }

  /**
   * Gets a join configuration with no separators, prefix or suffix.
   *
   * @return the join configuration
   * @since 4.9.0
   */
  static @NotNull JoinConfiguration noSeparators() {
    return JoinConfigurationImpl.NULL;
  }

  /**
   * Creates a join configuration with a separator and no prefix or suffix.
   *
   * @param separator the separator
   * @return the join configuration
   * @since 4.9.0
   */
  static @NotNull JoinConfiguration separator(final @Nullable ComponentLike separator) {
    if (separator == null) return JoinConfigurationImpl.NULL;
    return builder().separator(separator).build();
  }

  /**
   * Creates a join configuration with a separator and last separator but no prefix or suffix.
   *
   * @param separator the separator
   * @param lastSeparator the last separator
   * @return the join configuration
   * @since 4.9.0
   */
  static @NotNull JoinConfiguration separators(final @Nullable ComponentLike separator, final @Nullable ComponentLike lastSeparator) {
    if (separator == null && lastSeparator == null) return JoinConfigurationImpl.NULL;
    return builder().separator(separator).lastSeparator(lastSeparator).build();
  }

  /**
   * Gets the prefix of this join configuration.
   *
   * @return the prefix
   * @since 4.9.0
   */
  @Nullable Component prefix();

  /**
   * Gets the suffix of this join configuration.
   *
   * @return the suffix
   * @since 4.9.0
   */
  @Nullable Component suffix();

  /**
   * Gets the separator of this join configuration.
   *
   * @return the separator
   * @since 4.9.0
   */
  @Nullable Component separator();

  /**
   * Gets the last separator of this join configuration.
   *
   * @return the last separator
   * @since 4.9.0
   */
  @Nullable Component lastSeparator();

  /**
   * Gets the last separator that will be used instead of the normal last separator in the case where there
   * are more than two components being joined. This can be used to mimic a serial (or Oxford) comma.
   *
   * @return the separator
   * @since 4.9.0
   */
  @Nullable Component lastSeparatorIfSerial();

  /**
   * Gets the operator of this join configuration.
   *
   * <p>This is used to change the components that are going to be joined. It does not touch the prefix, suffix or any of the separators.</p>
   *
   * @return the operator
   * @since 4.9.0
   */
  @NotNull UnaryOperator<Component> operator();

  /**
   * A builder for join configurations.
   *
   * @since 4.9.0
   */
  interface Builder extends Buildable.Builder<JoinConfiguration> {
    /**
     * Sets the prefix of this join configuration builder.
     *
     * @param prefix the prefix
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder prefix(final @Nullable ComponentLike prefix);

    /**
     * Sets the suffix of this join configuration builder.
     *
     * @param suffix the suffix
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder suffix(final @Nullable ComponentLike suffix);

    /**
     * Sets the separator of this join configuration builder.
     *
     * @param separator the separator
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder separator(final @Nullable ComponentLike separator);

    /**
     * Sets the last separator of this join configuration builder.
     *
     * @param lastSeparator the last separator
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder lastSeparator(final @Nullable ComponentLike lastSeparator);

    /**
     * Sets the last separator that will be used instead of the normal last separator in the case where there
     * are more than two components being joined. This can be used to mimic a serial (or Oxford) comma.
     *
     * @param lastSeparatorIfSerial the last separator
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder lastSeparatorIfSerial(final @Nullable ComponentLike lastSeparatorIfSerial);

    /**
     * Sets the operator of this join configuration builder.
     *
     * <p>This is used to mutate the components that are going to be joined. It does not touch the prefix, suffix or any of the separators.</p>
     *
     * @param operator the operator
     * @return this builder
     * @since 4.9.0
     */
    @Contract("_ -> this")
    @NotNull Builder operator(final @NotNull UnaryOperator<Component> operator);
  }
}
