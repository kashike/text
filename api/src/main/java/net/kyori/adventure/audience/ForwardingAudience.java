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
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.jetbrains.annotations.ApiStatus;

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
  @NonNull Iterable<? extends Audience> audiences();

  @Override
  default <T> @NonNull Optional<T> get(final @NonNull Pointer<T> pointer) {
    return Optional.empty(); // unsupported
  }

  @Override
  default <T> @PolyNull T getOrDefault(final @NonNull Pointer<T> pointer, final @PolyNull T defaultValue) {
    return defaultValue; // unsupported
  }

  @Override
  default <T> @PolyNull T getOrDefaultFrom(final @NonNull Pointer<T> pointer, final @NonNull Supplier<? extends T> defaultValue) {
    return defaultValue.get(); // unsupported
  }

  @Override
  default void sendMessage(final @NonNull Identified source, final @NonNull Component message, final @NonNull MessageType type) {
    for (final Audience audience : this.audiences()) audience.sendMessage(source, message, type);
  }

  @Override
  default void sendMessage(final @NonNull Identity source, final @NonNull Component message, final @NonNull MessageType type) {
    for (final Audience audience : this.audiences()) audience.sendMessage(source, message, type);
  }

  @Override
  default void sendActionBar(final @NonNull Component message) {
    for (final Audience audience : this.audiences()) audience.sendActionBar(message);
  }

  @Override
  default void sendPlayerListHeader(final @NonNull Component header) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListHeader(header);
  }

  @Override
  default void sendPlayerListFooter(final @NonNull Component footer) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListFooter(footer);
  }

  @Override
  default void sendPlayerListHeaderAndFooter(final @NonNull Component header, final @NonNull Component footer) {
    for (final Audience audience : this.audiences()) audience.sendPlayerListHeaderAndFooter(header, footer);
  }

  @Override
  default void showTitle(final @NonNull Title title) {
    for (final Audience audience : this.audiences()) audience.showTitle(title);
  }

  @Override
  default void showTitle(final @NonNull Title title, final Title.@NonNull Part@NonNull... parts) {
    for(final Audience audience : this.audiences()) audience.showTitle(title, parts);
  }

  @Override
  default void showTitle(final @NonNull Title title, final @NonNull Iterable<Title.Part> parts) {
    for(final Audience audience : this.audiences()) audience.showTitle(title, parts);
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
  default void showBossBar(final @NonNull BossBar bar) {
    for (final Audience audience : this.audiences()) audience.showBossBar(bar);
  }

  @Override
  default void hideBossBar(final @NonNull BossBar bar) {
    for (final Audience audience : this.audiences()) audience.hideBossBar(bar);
  }

  @Override
  default void playSound(final @NonNull Sound sound) {
    for (final Audience audience : this.audiences()) audience.playSound(sound);
  }

  @Override
  default void playSound(final @NonNull Sound sound, final double x, final double y, final double z) {
    for (final Audience audience : this.audiences()) audience.playSound(sound, x, y, z);
  }

  @Override
  default void stopSound(final @NonNull SoundStop stop) {
    for (final Audience audience : this.audiences()) audience.stopSound(stop);
  }

  @Override
  default void openBook(final @NonNull Book book) {
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
    @NonNull Audience audience();

    /**
     * {@inheritDoc}
     *
     * @return {@link #audience()}
     * @deprecated this audience only supports forwarding to a single audience
     */
    @Deprecated(/* forRemoval = false */)
    @Override
    default @NonNull Iterable<? extends Audience> audiences() {
      return Collections.singleton(this.audience());
    }

    @Override
    default <T> @NonNull Optional<T> get(final @NonNull Pointer<T> pointer) {
      return this.audience().get(pointer);
    }

    @Override
    default <T> @PolyNull T getOrDefault(final @NonNull Pointer<T> pointer, final @PolyNull T defaultValue) {
      return this.audience().getOrDefault(pointer, defaultValue);
    }

    @Override
    default <T> @PolyNull T getOrDefaultFrom(final @NonNull Pointer<T> pointer, final @NonNull Supplier<? extends T> defaultValue) {
      return this.audience().getOrDefaultFrom(pointer, defaultValue);
    }

    @Override
    default void sendMessage(final @NonNull Identified source, final @NonNull Component message, final @NonNull MessageType type) {
      this.audience().sendMessage(source, message, type);
    }

    @Override
    default void sendMessage(final @NonNull Identity source, final @NonNull Component message, final @NonNull MessageType type) {
      this.audience().sendMessage(source, message, type);
    }

    @Override
    default void sendActionBar(final @NonNull Component message) {
      this.audience().sendActionBar(message);
    }

    @Override
    default void sendPlayerListHeader(final @NonNull Component header) {
      this.audience().sendPlayerListHeader(header);
    }

    @Override
    default void sendPlayerListFooter(final @NonNull Component footer) {
      this.audience().sendPlayerListFooter(footer);
    }

    @Override
    default void sendPlayerListHeaderAndFooter(final @NonNull Component header, final @NonNull Component footer) {
      this.audience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    default void showTitle(final @NonNull Title title) {
      this.audience().showTitle(title);
    }

    @Override
    default void showTitle(final @NonNull Title title, final Title.@NonNull Part@NonNull... parts) {
      this.audience().showTitle(title, parts);
    }

    @Override
    default void showTitle(final @NonNull Title title, final @NonNull Iterable<Title.Part> parts) {
      this.audience().showTitle(title, parts);
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
    default void showBossBar(final @NonNull BossBar bar) {
      this.audience().showBossBar(bar);
    }

    @Override
    default void hideBossBar(final @NonNull BossBar bar) {
      this.audience().hideBossBar(bar);
    }

    @Override
    default void playSound(final @NonNull Sound sound) {
      this.audience().playSound(sound);
    }

    @Override
    default void playSound(final @NonNull Sound sound, final double x, final double y, final double z) {
      this.audience().playSound(sound, x, y, z);
    }

    @Override
    default void stopSound(final @NonNull SoundStop stop) {
      this.audience().stopSound(stop);
    }

    @Override
    default void openBook(final @NonNull Book book) {
      this.audience().openBook(book);
    }
  }
}
