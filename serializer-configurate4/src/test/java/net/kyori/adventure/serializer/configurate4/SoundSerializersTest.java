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

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SoundSerializersTest implements ConfigurateTestBase {
  @Test
  void testSound() {
    final ConfigurationNode sound = this.node(n -> {
      n.node(SoundSerializer.NAME).raw("minecraft:music_disc.cat");
      n.node(SoundSerializer.SOURCE).raw("ambient");
      n.node(SoundSerializer.VOLUME).raw(0.8f);
      n.node(SoundSerializer.PITCH).raw(2.0f);
    });
    final Sound deserialized = Sound.sound(Key.key("music_disc.cat"), Sound.Source.AMBIENT, 0.8f, 2.0f);
    this.assertRoundtrippable(Sound.class, deserialized, sound);
  }

  @Test
  void testSoundNoVolume() throws SerializationException {
    final ConfigurationNode sound = this.node(n -> {
      n.node(SoundSerializer.NAME).raw("minecraft:music_disc.cat");
      n.node(SoundSerializer.SOURCE).raw("ambient");
      n.node(SoundSerializer.PITCH).raw(2.0f);
    });
    final Sound deserialized = Sound.sound(Key.key("music_disc.cat"), Sound.Source.AMBIENT, 1.0f, 2.0f);
    assertEquals(deserialized, sound.get(Sound.class));
  }

  @Test
  void testSoundNoPitch() throws SerializationException {
    final ConfigurationNode sound = this.node(n -> {
      n.node(SoundSerializer.NAME).raw("minecraft:music_disc.cat");
      n.node(SoundSerializer.SOURCE).raw("ambient");
      n.node(SoundSerializer.VOLUME).raw(0.8f);
    });
    final Sound deserialized = Sound.sound(Key.key("music_disc.cat"), Sound.Source.AMBIENT, 0.8f, 1.0f);
    assertEquals(deserialized, sound.get(Sound.class));
  }

  @Test
  void testNoNameThrows() {
    assertThrows(SerializationException.class, () -> this.node(n -> n.node(SoundSerializer.SOURCE).set("music")).get(Sound.class));
  }

  @Test
  void testNoSourceThrows() {
    assertThrows(SerializationException.class, () -> this.node(n -> n.node(SoundSerializer.NAME).set("music_disc.13")).get(Sound.class));
  }

  @Test
  void testSoundStopAll() {
    final ConfigurationNode node = this.node(ImmutableMap.of());
    final SoundStop stop = SoundStop.all();

    this.assertRoundtrippable(SoundStop.class, stop, node);
  }

  @Test
  void testSoundStopName() {
    final ConfigurationNode node = this.node(n -> {
      n.node(SoundStopSerializer.SOUND).raw("minecraft:music_disc.pigstep"); // jk who would want to stop pigstep
    });
    final SoundStop stop = SoundStop.named(Key.key("minecraft:music_disc.pigstep"));

    this.assertRoundtrippable(SoundStop.class, stop, node);
  }

  @Test
  void testSoundStopSource() {
    final ConfigurationNode node = this.node(n -> {
      n.node(SoundStopSerializer.SOURCE).raw("hostile");
    });
    final SoundStop stop = SoundStop.source(Sound.Source.HOSTILE);

    this.assertRoundtrippable(SoundStop.class, stop, node);
  }

  @Test
  void testSoundStopNameAndSource() {
    final ConfigurationNode node = this.node(n -> {
      n.node(SoundStopSerializer.SOUND).raw("minecraft:entity.cat.hiss");
      n.node(SoundStopSerializer.SOURCE).raw("ambient");
    });
    final SoundStop stop = SoundStop.namedOnSource(Key.key("minecraft", "entity.cat.hiss"), Sound.Source.AMBIENT);

    this.assertRoundtrippable(SoundStop.class, stop, node);
  }
}
