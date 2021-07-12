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
package net.kyori.adventure.text;

import java.util.Collections;
import java.util.stream.IntStream;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinTest {
  @Test
  void testJoin() {
    final JoinConfiguration config = JoinConfiguration.separator(Component.space());

    assertEquals(Component.empty(), Component.join(JoinConfiguration.separator(Component.space()), Collections.emptyList()));

    final Component c0 = Component.text("test");
    assertEquals(c0, Component.join(config, c0));

    final Component c1 = Component.join(
      config,
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0))
        .append(Component.space())
        .append(Component.text(1))
        .append(Component.space())
        .append(Component.text(2))
        .build(),
      c1
    );
  }

  @Test
  void testJoinWithFinalSeparator() {
    final JoinConfiguration config = JoinConfiguration.separators(Component.space(), Component.text(" and "));

    assertEquals(Component.empty(), Component.join(config, Collections.emptyList()));

    final Component c0 = Component.text("test");
    assertEquals(c0, Component.join(config, c0));

    final Component c1 = Component.join(
      config,
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0))
        .append(Component.space())
        .append(Component.text(1))
        .append(Component.text(" and "))
        .append(Component.text(2))
        .build(),
      c1
    );

    final Component c2 = Component.join(
      config,
      Component.text(0),
      Component.text(1)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0))
        .append(Component.text(" and "))
        .append(Component.text(1))
        .build(),
      c2
    );
  }

  @Test
  void testJoinWithPrefixSuffix() {
    final JoinConfiguration config = JoinConfiguration.builder()
      .separator(Component.space())
      .prefix(Component.text("prefix"))
      .suffix(Component.text("suffix"))
      .build();

    assertEquals(
      Component.text()
        .append(Component.text("prefix"))
        .append(Component.text("suffix"))
        .build(),
      Component.join(config, Collections.emptyList())
    );

    final Component c0 = Component.join(
      config,
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text("prefix"))
        .append(Component.text(0))
        .append(Component.space())
        .append(Component.text(1))
        .append(Component.space())
        .append(Component.text(2))
        .append(Component.text("suffix"))
        .build(),
      c0
    );
  }

  @Test
  void testJoinWithOperator() {
    final JoinConfiguration config = JoinConfiguration.builder()
      .separator(Component.space())
      .operator(component -> component.color(NamedTextColor.RED))
      .build();

    assertEquals(Component.empty(), Component.join(JoinConfiguration.separator(Component.space()), Collections.emptyList()));

    final Component c0 = Component.text("test");
    assertEquals(c0.color(NamedTextColor.RED), Component.join(config, c0));

    final Component c1 = Component.join(
      config,
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0, NamedTextColor.RED))
        .append(Component.space())
        .append(Component.text(1, NamedTextColor.RED))
        .append(Component.space())
        .append(Component.text(2, NamedTextColor.RED))
        .build(),
      c1
    );
  }

  @Test
  void testJoinWithNoSeparators() {
    final JoinConfiguration config = JoinConfiguration.noSeparators();

    assertEquals(Component.empty(), Component.join(config, Collections.emptyList()));

    final Component c0 = Component.text("test");
    assertEquals(c0, Component.join(config, c0));

    final Component c1 = Component.join(
      config,
      IntStream.range(0, 3)
        .mapToObj(Component::text)
        .toArray(Component[]::new)
    );
    assertEquals(
      Component.text()
        .append(Component.text(0))
        .append(Component.text(1))
        .append(Component.text(2))
        .build(),
      c1
    );
  }

  @Test
  void testJoinWithSerialComma() {
    final Component comma = Component.text(", ");
    final Component and = Component.text(" and ");
    final Component serialAnd = Component.text(", and ");

    final JoinConfiguration config = JoinConfiguration.builder()
      .separator(comma)
      .lastSeparator(and)
      .lastSeparatorIfSerial(serialAnd)
      .build();

    assertEquals(Component.empty(), Component.join(config, Collections.emptyList()));

    final Component c0 = Component.text("test");
    assertEquals(c0, Component.join(config, c0));

    final Component[] numbers = IntStream.range(0, 3)
      .mapToObj(Component::text)
      .toArray(Component[]::new);

    final Component c1 = Component.join(
      config,
      numbers[0],
      numbers[1]
    );
    assertEquals(
      Component.text()
        .append(numbers[0])
        .append(and)
        .append(numbers[1])
        .build(),
      c1
    );

    final Component c2 = Component.join(config, numbers);
    assertEquals(
      Component.text()
        .append(numbers[0])
        .append(comma)
        .append(numbers[1])
        .append(serialAnd)
        .append(numbers[2])
        .build(),
      c2
    );
  }
}
