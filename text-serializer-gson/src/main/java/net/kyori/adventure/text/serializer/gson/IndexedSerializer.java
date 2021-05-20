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
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Index;

final class IndexedSerializer<E> extends TypeAdapter<E> {
  public static final TypeAdapter<ClickEvent.Action> CLICK_ACTION_SERIALIZER = of("click action", ClickEvent.Action.NAMES);
  public static final TypeAdapter<HoverEvent.Action<?>> HOVER_ACTION_SERIALIZER = of("hover action", HoverEvent.Action.NAMES);
  public static final TypeAdapter<TextDecoration> TEXT_DECORATION_SERIALIZER = of("text decoration", TextDecoration.NAMES);

  private final String name;
  private final Index<String, E> map;

  public static <E> TypeAdapter<E> of(final String name, final Index<String, E> map) {
    return new IndexedSerializer<>(name, map).nullSafe();
  }

  private IndexedSerializer(final String name, final Index<String, E> map) {
    this.name = name;
    this.map = map;
  }

  @Override
  public void write(final JsonWriter out, final E value) throws IOException {
    out.value(this.map.key(value));
  }

  @Override
  public E read(final JsonReader in) throws IOException {
    final String string = in.nextString();
    final E value = this.map.value(string);
    if(value != null) {
      return value;
    } else {
      throw new JsonParseException("invalid " + this.name + ":  " + string);
    }
  }
}
