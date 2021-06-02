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
package net.kyori.adventure.sound;

import com.google.common.testing.EqualsTester;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static net.kyori.adventure.sound.Sound.sound;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SoundTest {
  private static final Key SOUND_KEY = Key.key("minecraft", "block.fence_gate.open");
  private static final Sound.Type SOUND_TYPE = () -> SOUND_KEY;

  @Test
  void testGetters() {
    final Sound sound = sound(SOUND_KEY, Source.HOSTILE, 1f, 1f);
    assertEquals(SOUND_KEY, sound.name());
    assertEquals(Source.HOSTILE, sound.source());
    assertEquals(1f, sound.volume());
    assertEquals(1f, sound.pitch());
  }

  @Test
  void testOfIsEqual() {
    new EqualsTester()
      .addEqualityGroup(
        sound(SOUND_KEY, Source.HOSTILE, 1f, 1f),
        sound(SOUND_TYPE, Source.HOSTILE, 1f, 1f),
        sound(() -> SOUND_TYPE, Source.HOSTILE, 1f, 1f)
      )
      .testEquals();
  }

  @Test
  void testInitializers_notNull() {
    assertNotNull(sound(SOUND_KEY, () -> Source.HOSTILE, 1F, 1F));
    assertNotNull(sound(SOUND_TYPE, Source.HOSTILE, 1F, 1F));
    assertNotNull(sound(() -> SOUND_TYPE, () -> Source.HOSTILE, 1F, 1F));
  }

  @Test
  void testIndices() {
    assertEquals(Source.MASTER.name().toLowerCase(Locale.ROOT), Source.NAMES.key(Source.MASTER));
  }
}
