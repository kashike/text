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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScalarSerializersTest implements ConfigurateTestBase {
  @Test
  void testSerializeNamedTextColor() throws SerializationException {
    final ConfigurationNode node = this.node();
    node.set(TextColor.class, NamedTextColor.AQUA);
    assertEquals("aqua", node.getString());

    node.set("dark_purple");
    assertEquals(NamedTextColor.DARK_PURPLE, node.get(TextColorSerializer.INSTANCE.type()));
  }

  @Test
  void testSerializeHexTextColor() throws SerializationException {
    // read as hex string
    final ConfigurationNode node = this.node("#adface");
    assertEquals(TextColor.color(0xadface), node.get(TextColor.class));

    // read as int
    node.set(0x2468AC);
    assertEquals(TextColor.color(0x2468AC), node.get(TextColor.class));

    node.set(TextColor.class, TextColor.color(0x123456));
    assertEquals("#123456", node.getString());
  }

  @Test
  void testSerializerKey() throws SerializationException {
    assertThrows(SerializationException.class, () -> {
      this.node("MineCRaft:test/namespace-invalid.gif").get(Key.class);
    }, "namespace");
    assertThrows(SerializationException.class, () -> {
      this.node("minecraft:test path invalid.gif").get(Key.class);
    }, "path");
    assertEquals(Key.key("test/valid.wav"), this.node("minecraft:test/valid.wav").get(Key.class));
  }

  @Test
  void testSerializeKeyCustomNamespace() throws SerializationException {
    assertEquals(Key.key("adventure", "meow"), this.node("adventure:meow").get(Key.class));
  }

  @Test
  void testSerializeDuration() throws SerializationException {
    assertEquals("PT240H", this.durationTo(Duration.ofDays(10)));
    assertEquals(Duration.ofDays(10), this.durationFrom("10d"));
    assertEquals(Duration.ofSeconds(5).plus(50, ChronoUnit.MILLIS), this.durationFrom("PT5.05S"));
  }

  private Duration durationFrom(final String value) throws SerializationException {
    return this.node(value).get(DurationSerializer.INSTANCE.type());
  }

  private String durationTo(final Duration duration) throws SerializationException {
    return this.node().set(DurationSerializer.INSTANCE.type(), duration).getString();
  }
}
