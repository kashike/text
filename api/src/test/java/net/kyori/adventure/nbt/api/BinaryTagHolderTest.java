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
package net.kyori.adventure.nbt.api;

import net.kyori.adventure.util.Codec;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BinaryTagHolderTest {
  private static final Codec.Decoder<String, String, Exception> NOOP_DECODER = new Codec.Decoder<String, String, Exception>() {
    @Override
    public @NonNull String decode(@NonNull String encoded) throws Exception {
      return "";
    }
  };
  private static final Codec.Encoder<String, String, Exception> NOOP_ENCODER = new Codec.Encoder<String, String, Exception>() {
    @Override
    public @NonNull String encode(@NonNull String decoded) throws Exception {
      return "";
    }
  };
  private static final Codec<String, String, Exception, Exception> NOOP_CODEC = Codec.of(NOOP_DECODER, NOOP_ENCODER);

  @Test
  void testInitializers_notNull() throws Exception {
    assertNotNull(BinaryTagHolder.encode("", NOOP_CODEC));
    assertNotNull(BinaryTagHolder.of(""));
  }

  @Test
  void testValues_notNull() throws Exception {
    assertNotNull(BinaryTagHolder.of("").string());
    assertEquals("hello world!", BinaryTagHolder.of("hello world!").string());
    assertNotNull(BinaryTagHolder.of("").get(NOOP_CODEC));
  }
}
