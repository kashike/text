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
package net.kyori.adventure.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.MonkeyBars;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.util.Objects.requireNonNull;

final class BookImpl implements Book {
  private final Component title;
  private final Component author;
  private final List<Component> pages;

  BookImpl(final @NonNull Component title, final @NonNull Component author, final @NonNull List<Component> pages) {
    this.title = requireNonNull(title, "title");
    this.author = requireNonNull(author, "author");
    this.pages = requireNonNull(pages, "pages");
  }

  @Override
  public @NonNull Component title() {
    return this.title;
  }

  @Override
  public @NonNull Book title(final @NonNull Component title) {
    return new BookImpl(requireNonNull(title, "title"), this.author, this.pages);
  }

  @Override
  public @NonNull Component author() {
    return this.author;
  }

  @Override
  public @NonNull Book author(final @NonNull Component author) {
    return new BookImpl(this.title, requireNonNull(author, "author"), this.pages);
  }

  @Override
  public @NonNull List<Component> pages() {
    return this.pages;
  }

  @Override
  public @NonNull Book pages(final @NonNull List<Component> pages) {
    return new BookImpl(this.title, this.author, new ArrayList<>(requireNonNull(pages, "pages")));
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("title", this.title),
      ExaminableProperty.of("author", this.author),
      ExaminableProperty.of("pages", this.pages)
    );
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(!(o instanceof BookImpl)) return false;
    final BookImpl that = (BookImpl) o;
    return this.title.equals(that.title)
      && this.author.equals(that.author)
      && this.pages.equals(that.pages);
  }

  @Override
  public int hashCode() {
    int result = this.title.hashCode();
    result = 31 * result + this.author.hashCode();
    result = 31 * result + this.pages.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }

  static final class BuilderImpl implements Book.Builder {
    private Component title = Component.empty();
    private Component author = Component.empty();
    private final List<Component> pages = new ArrayList<>();

    @Override
    public @NonNull Builder title(final @NonNull Component title) {
      this.title = requireNonNull(title, "title");
      return this;
    }

    @Override
    public @NonNull Builder author(final @NonNull Component author) {
      this.author = requireNonNull(author, "author");
      return this;
    }

    @Override
    public @NonNull Builder addPage(final @NonNull Component page) {
      this.pages.add(requireNonNull(page, "page"));
      return this;
    }

    @Override
    public @NonNull Builder pages(final @NonNull Collection<Component> pages) {
      this.pages.addAll(requireNonNull(pages, "pages"));
      return this;
    }

    @Override
    public @NonNull Builder pages(final @NonNull Component@NonNull... pages) {
      Collections.addAll(this.pages, pages);
      return this;
    }

    @Override
    public @NonNull Book build() {
      return new BookImpl(this.title, this.author, MonkeyBars.immutableList(this.pages));
    }
  }
}
