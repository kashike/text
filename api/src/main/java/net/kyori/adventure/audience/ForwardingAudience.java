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
package net.kyori.adventure.audience;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * A receiver that wraps one or more receivers.
 *
 * <p><code>ForwardingAudience</code> is designed to easily allow users or
 * implementations wrap an existing (collection of) <code>Audience</code>(s).</p>
 *
 * @see Audience
 * @since 4.0.0
 */
@FunctionalInterface
public interface ForwardingAudience extends Audience {
  /**
   * Gets the audiences.
   *
   * @return the audiences
   * @since 4.0.0
   */
  @ApiStatus.OverrideOnly
  @NotNull Iterable<? extends Audience> audiences();

  @Override
  default <T> @NotNull Optional<T> get(final @NotNull Pointer<T> pointer) {
    return Optional.empty(); // unsupported
  }

  @Contract("_, null -> null; _, !null -> !null")
  @Override
  default <T> @Nullable T getOrDefault(final @NotNull Pointer<T> pointer, final @Nullable T defaultValue) {
    return defaultValue; // unsupported
  }

  @Override
  default <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
    return defaultValue.get(); // unsupported
  }

  @Override
  default void sendMessage(final @NotNull Identified source, final @NotNull Component message, final @NotNull MessageType type) {
    for (final Audience audience : this.audiences()) audience.sendMessage(source, message, type);
  }

  @Override
  default void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
    for (final Audience audience : this.audiences()) audience.sendMessage(source, message, type);
  }

  @Override
  default void sendActionBar(final @NotNull Component message) {
    for (final Audience audience : this.audiences()) audience.sendActionBar(message);
  }

  @Override
  default void sendPlayerListHeader(final @NotNull Component header) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListHeader(header);
  }

  @Override
  default void sendPlayerListFooter(final @NotNull Component footer) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListFooter(footer);
  }

  @Override
  default void sendPlayerListHeaderAndFooter(final @NotNull Component header, final @NotNull Component footer) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListHeaderAndFooter(header, footer);
  }

  @Override
  default <T> void sendTitlePart(final @NotNull TitlePart<T> titlePart, @NotNull final T content) {
    for (final Audience audience : this.audiences()) audience.sendTitlePart(titlePart, content);
  }

  @Override
  default void clearTitle() {
    for (final Audience audience : this.audiences()) audience.clearTitle();
  }

  @Override
  default void resetTitle() {
    for (final Audience audience : this.audiences()) audience.resetTitle();
  }

  @Override
  default void showBossBar(final @NotNull BossBar bar) {
    for (final Audience audience : this.audiences()) audience.showBossBar(bar);
  }

  @Override
  default void hideBossBar(final @NotNull BossBar bar) {
    for (final Audience audience : this.audiences()) audience.hideBossBar(bar);
  }

  @Override
  default void playSound(final @NotNull Sound sound) {
    for (final Audience audience : this.audiences()) audience.playSound(sound);
  }

  @Override
  default void playSound(final @NotNull Sound sound, final double x, final double y, final double z) {
    for (final Audience audience : this.audiences()) audience.playSound(sound, x, y, z);
  }

  @Override
  default void playSound(final @NotNull Sound sound, final Sound.@NotNull Emitter emitter) {
    for (final Audience audience : this.audiences()) audience.playSound(sound, emitter);
  }

  @Override
  default void stopSound(final @NotNull SoundStop stop) {
    for (final Audience audience : this.audiences()) audience.stopSound(stop);
  }

  @Override
  default void openBook(final @NotNull Book book) {
    for (final Audience audience : this.audiences()) audience.openBook(book);
  }

  /**
   * An audience that forwards everything to a single other audience.
   *
   * @since 4.0.0
   */
  interface Single extends ForwardingAudience {
    /**
     * Gets the audience.
     *
     * @return the audience
     * @since 4.0.0
     */
    @ApiStatus.OverrideOnly
    @NotNull Audience audience();

    /**
     * {@inheritDoc}
     *
     * @return {@link #audience()}
     * @deprecated this audience only supports forwarding to a single audience
     */
    @Deprecated(/* forRemoval = false */)
    @Override
    default @NotNull Iterable<? extends Audience> audiences() {
      return Collections.singleton(this.audience());
    }

    @Override
    default <T> @NotNull Optional<T> get(final @NotNull Pointer<T> pointer) {
      return this.audience().get(pointer);
    }

    @Contract("_, null -> null; _, !null -> !null")
    @Override
    default <T> @Nullable T getOrDefault(final @NotNull Pointer<T> pointer, final @Nullable T defaultValue) {
      return this.audience().getOrDefault(pointer, defaultValue);
    }

    @Override
    default <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
      return this.audience().getOrDefaultFrom(pointer, defaultValue);
    }

    @Override
    default @NotNull Pointers pointers() {
      return this.audience().pointers();
    }

    @Override
    default void sendMessage(final @NotNull Identified source, final @NotNull Component message, final @NotNull MessageType type) {
      this.audience().sendMessage(source, message, type);
    }

    @Override
    default void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
      this.audience().sendMessage(source, message, type);
    }

    @Override
    default void sendActionBar(final @NotNull Component message) {
      this.audience().sendActionBar(message);
    }

    @Override
    default void sendPlayerListHeader(final @NotNull Component header) {
      this.audience().sendPlayerListHeader(header);
    }

    @Override
    default void sendPlayerListFooter(final @NotNull Component footer) {
      this.audience().sendPlayerListFooter(footer);
    }

    @Override
    default void sendPlayerListHeaderAndFooter(final @NotNull Component header, final @NotNull Component footer) {
      this.audience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    default <T> void sendTitlePart(final @NotNull TitlePart<T> titlePart, @NotNull final T content) {
      this.audience().sendTitlePart(titlePart, content);
    }

    @Override
    default void clearTitle() {
      this.audience().clearTitle();
    }

    @Override
    default void resetTitle() {
      this.audience().resetTitle();
    }

    @Override
    default void showBossBar(final @NotNull BossBar bar) {
      this.audience().showBossBar(bar);
    }

    @Override
    default void hideBossBar(final @NotNull BossBar bar) {
      this.audience().hideBossBar(bar);
    }

    @Override
    default void playSound(final @NotNull Sound sound) {
      this.audience().playSound(sound);
    }

    @Override
    default void playSound(final @NotNull Sound sound, final double x, final double y, final double z) {
      this.audience().playSound(sound, x, y, z);
    }

    @Override
    default void playSound(final @NotNull Sound sound, final Sound.@NotNull Emitter emitter) {
      this.audience().playSound(sound, emitter);
    }

    @Override
    default void stopSound(final @NotNull SoundStop stop) {
      this.audience().stopSound(stop);
    }

    @Override
    default void openBook(final @NotNull Book book) {
      this.audience().openBook(book);
    }
  }
}
