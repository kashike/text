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
package net.kyori.adventure.serializer.configurate4;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

final class TextColorSerializer extends ScalarSerializer<TextColor> {
  static final TextColorSerializer INSTANCE = new TextColorSerializer();
  private static final String HEX_PREFIX = "#";

  private TextColorSerializer() {
    super(TextColor.class);
  }

  @Override
  public TextColor deserialize(final @NonNull Type type, final @NonNull Object obj) throws SerializationException {
    if(obj instanceof Number) { // numerical values
      return TextColor.color(((Number) obj).intValue());
    } else if(!(obj instanceof CharSequence)) {
      throw new SerializationException("Text colors must either be strings or integers");
    }
    final String value = obj.toString();
    final TextColor result;
    if(value.startsWith(HEX_PREFIX)) {
      result = TextColor.fromHexString(value);
    } else {
      result = NamedTextColor.NAMES.value(value);
    }
    if(result == null) {
      throw new SerializationException("Could not convert '" + value + "' into a TextColor");
    }
    return result;
  }

  @Override
  public Object serialize(final @NonNull TextColor item, final @NonNull Predicate<Class<?>> typeSupported) {
    if(item instanceof NamedTextColor) { // TODO: Downsampling
      return NamedTextColor.NAMES.key((NamedTextColor) item);
    } else {
      return item.asHexString();
    }
  }
}
