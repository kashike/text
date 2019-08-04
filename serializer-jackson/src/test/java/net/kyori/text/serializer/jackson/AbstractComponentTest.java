/*
 * This file is part of text, licensed under the MIT License.
 *
 * Copyright (c) 2017-2019 KyoriPowered
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
package net.kyori.text.serializer.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.reflect.TypeToken;
import net.kyori.text.Component;

import java.io.IOException;

abstract class AbstractComponentTest<C extends Component> extends AbstractSerializeDeserializeTest<C> {
  @SuppressWarnings("serial")
  private final TypeToken<C> type = new TypeToken<C>(getClass()) {};

  @Override
  @SuppressWarnings("unchecked")
  C deserialize(final JsonNode json) {
    try {
      return (C) JacksonComponentSerializer.MAPPER.treeToValue(json, this.type.getRawType());
    } catch (IOException e) {
      throw new RuntimeException("Can't deserialize", e);
    }
  }

  @Override
  JsonNode serialize(final C object) {
    return JacksonComponentSerializer.MAPPER.valueToTree(object);
  }
}
