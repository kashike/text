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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class AlwaysMerger implements Merger {
  static final AlwaysMerger INSTANCE = new AlwaysMerger();

  private AlwaysMerger() {
  }

  @Override
  public void mergeColor(final StyleImpl.BuilderImpl target, final @Nullable TextColor color) {
    target.color(color);
  }

  @Override
  public void mergeDecoration(final StyleImpl.BuilderImpl target, final @NonNull TextDecoration decoration, final TextDecoration.@NonNull State state) {
    target.decoration(decoration, state);
  }

  @Override
  public void mergeClickEvent(final StyleImpl.BuilderImpl target, final @Nullable ClickEvent event) {
    target.clickEvent(event);
  }

  @Override
  public void mergeHoverEvent(final StyleImpl.BuilderImpl target, final @Nullable HoverEvent<?> event) {
    target.hoverEvent(event);
  }

  @Override
  public void mergeInsertion(final StyleImpl.BuilderImpl target, final @Nullable String insertion) {
    target.insertion(insertion);
  }

  @Override
  public void mergeFont(final StyleImpl.BuilderImpl target, final @Nullable Key font) {
    target.font(font);
  }
}
