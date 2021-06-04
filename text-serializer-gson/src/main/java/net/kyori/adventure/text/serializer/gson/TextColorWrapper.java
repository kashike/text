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
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.Nullable;

/*
 * This is a hack.
 */
final class TextColorWrapper {
  final @Nullable TextColor color;
  final @Nullable TextDecoration decoration;
  final boolean reset;

  TextColorWrapper(final @Nullable TextColor color, final @Nullable TextDecoration decoration, final boolean reset) {
    this.color = color;
    this.decoration = decoration;
    this.reset = reset;
  }

  static final class Serializer extends TypeAdapter<TextColorWrapper> {
    static final Serializer INSTANCE = new Serializer();

    private Serializer() {
    }

    @Override
    public void write(final JsonWriter out, final TextColorWrapper value) {
      throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
    }

    @Override
    public TextColorWrapper read(final JsonReader in) throws IOException {
      final String input = in.nextString();
      final TextColor color = TextColorSerializer.fromString(input);
      final TextDecoration decoration = TextDecoration.NAMES.value(input);
      final boolean reset = decoration == null && input.equals("reset");
      if(color == null && decoration == null && !reset) {
        throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
      }
      return new TextColorWrapper(color, decoration, reset);
    }
  }
}
