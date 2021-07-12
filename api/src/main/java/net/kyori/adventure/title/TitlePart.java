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
package net.kyori.adventure.title;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A part of a title.
 *
 * @param <T> the type of the content of the part
 * @see net.kyori.adventure.audience.Audience#sendTitlePart(TitlePart, Object)
 * @since 4.9.0
 */
@ApiStatus.NonExtendable
public interface TitlePart<T> {
  /**
   * A title part of a title.
   *
   * @since 4.9.0
   */
  @NotNull TitlePart<Component> TITLE = new TitlePart<Component>() {
    @Override
    public String toString() {
      return "TitleTitlePart";
    }
  };

  /**
   * The subtitle part of a title.
   *
   * @since 4.9.0
   */
  @NotNull TitlePart<Component> SUBTITLE = new TitlePart<Component>() {
    @Override
    public String toString() {
      return "SubtitleTitlePart";
    }
  };

  /**
   * The times part of a title.
   *
   * @since 4.8.0
   */
  @NotNull TitlePart<Title.Times> TIMES = new TitlePart<Title.Times>() {
    @Override
    public String toString() {
      return "TimesTitlePart";
    }
  };
}
