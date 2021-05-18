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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

final class ShowItemSerializer extends TypeAdapter<HoverEvent.ShowItem> {

  public static final TypeAdapter<HoverEvent.ShowItem> INSTANCE = new ShowItemSerializer().nullSafe();

  static final String ID = "id";
  static final String COUNT = "count";
  static final String TAG = "tag";

  private ShowItemSerializer() {
  }

  @Override
  public HoverEvent.ShowItem read(final JsonReader in) throws IOException {
    in.beginObject();

    Key key = null;
    int count = 1;
    BinaryTagHolder nbt = null;

    while(in.hasNext()) {
      switch(in.nextName()) {
        case ID:
          key = KeySerializer.INSTANCE.read(in);
          break;
        case COUNT:
          count = in.nextInt();
          break;
        case TAG:
          switch(in.peek()) {
            case STRING:
            case NUMBER:
              nbt = BinaryTagHolder.of(in.nextString());
              break;
            case BOOLEAN:
              nbt = BinaryTagHolder.of(String.valueOf(in.nextBoolean()));
              break;
            case NULL:
              in.nextNull();
              break;
            default:
              throw new JsonParseException("Expected " + TAG + " to be a string");
          }
          break;
        default:
          in.skipValue();
          break;
      }
    }

    if(key == null) {
      throw new JsonParseException("Not sure how to deserialize show_item hover event");
    }
    in.endObject();

    return HoverEvent.ShowItem.of(key, count, nbt);
  }

  @Override
  public void write(final JsonWriter out, final HoverEvent.ShowItem value) throws IOException {
    out.beginObject();

    out.name(ID);
    KeySerializer.INSTANCE.write(out, value.item());

    final int count = value.count();
    if(count != 1) {
      out.name(COUNT).value(count);
    }

    final @Nullable BinaryTagHolder nbt = value.nbt();
    if(nbt != null) {
      out.name(TAG).value(nbt.string());
    }

    out.endObject();
  }
}
