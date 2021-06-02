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
package net.kyori.adventure.pointer;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PointerTest {

  @Test
  void testInitializer() {
    assertNotNull(Pointer.pointer(Object.class, Key.key("empty")));
  }

  @Test
  void testValues() {
    final Key key = Key.key("empty");
    final Pointer<Object> pointer = new PointerImpl<>(Object.class, key);
    assertEquals(Object.class, pointer.type());
    assertEquals(key, pointer.key());
  }

  @Test
  void testPointered_getOrDefault() {
    final Pointered pointered = new Pointered() {};
    assertNotNull(pointered.getOrDefault(new PointerImpl<>(Object.class, Key.key("empty")), new Object()));
    assertNotNull(pointered.getOrDefaultFrom(new PointerImpl<>(Object.class, Key.key("empty")), Object::new));
  }
}
