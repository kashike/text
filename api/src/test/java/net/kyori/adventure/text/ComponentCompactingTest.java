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

import java.util.stream.Stream;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.Style.style;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ComponentCompactingTest {
  @TestFactory
  Stream<DynamicTest> testNestedComponentsWithSameStyleAreCombined() {
    final Style style = style()
      .color(NamedTextColor.AQUA)
      .font(key("uniform"))
      .decorate(TextDecoration.BOLD, TextDecoration.ITALIC)
      .build();

    return Stream.of(
      dynamicTest("child's style fully inherited", () -> {
        final Component input = text()
          .content("Hello ")
          .style(style)
          .append(text("World!"))
          .build();

        assertEquals(text("Hello World!", style), input.compact());
      }),
      dynamicTest("child's style partially redeclared, but effectively redundant", () -> {
        final Component input = text()
          .content("Hello ")
          .style(style)
          .append(text("World!", style(TextDecoration.BOLD).font(key("uniform"))))
          .build();

        assertEquals(text("Hello World!", style), input.compact());
      }),
      dynamicTest("multiple layers of nesting, with some children redeclaring styles", () -> {
        final Component input = text()
          .content("Hello ")
          .style(style)
          .append(text(c -> c.content("World! ")
            .decorate(TextDecoration.BOLD)
            .append(text("What a ")
              .append(text("beautiful day!", style(s -> s.font(key("uniform"))))))
          ))
          .build();

        assertEquals(text("Hello World! What a beautiful day!", style), input.compact());
      }),
      dynamicTest("multiple sibling children", () -> {
        final Component input = text()
          .content("Hello ")
          .style(style)
          .append(text("World! "))
          .append(text("What a ", style().font(key("uniform")).build()))
          .append(text("beautiful day!"))
          .build();

        assertEquals(text("Hello World! What a beautiful day!", style), input.compact());
      }),
      dynamicTest("multiple siblings across multiple layers", () -> {
        final Component input = text()
          .content("Hello ")
          .style(style)
          .append(text("World! "))
          .append(text("What a ", style().font(key("uniform")).build()))
          .append(text(c -> c.content("beautiful day ")
            .decorate(TextDecoration.BOLD)
            .append(text("to stay inside "))
            .append(text("and hone your "))
            .append(text("development skills!", style(TextDecoration.ITALIC)))
          ))
          .build();

        assertEquals(text("Hello World! What a beautiful day to stay inside and hone your development skills!", style), input.compact());
      })
    );
  }

  @TestFactory
  Stream<DynamicTest> testCompactingNestedComponentsInterruptedByDifferentlyStyledComponents() {
    final Style baseStyle = style(NamedTextColor.RED, TextDecoration.BOLD, TextDecoration.OBFUSCATED);
    return Stream.of(
      dynamicTest("simple component with most joinable, and an unjoinable in the middle", () -> {
        final Component component =
          text().content("Hello ")
            .style(baseStyle)
            .append(text("World! "))
            .append(text("What a ", NamedTextColor.RED))
            .append(text("beautiful day ", NamedTextColor.BLUE))
            .append(text("to create "))
            .append(text("a PR on Adventure!", style(TextDecoration.BOLD)))
            .build();

        assertEquals(
          text()
            .content("Hello World! What a ")
            .style(baseStyle)

            .append(text("beautiful day ", NamedTextColor.BLUE))
            .append(text("to create a PR on Adventure!"))
            .build(),
          component.compact());
      }),
      dynamicTest("simple component with joinable children, and an unjoinable child with joinable children", () -> {
        final Component component =
          text().content("Hello ")
            .style(baseStyle)

            .append(text("World! "))
            .append(text("What a ", NamedTextColor.RED))
            .append(text(c -> c.content("beautiful day ")
              .color(NamedTextColor.BLUE)
              .append(text("to create ", style(TextDecoration.ITALIC)))
              .append(text("a PR ", style(TextDecoration.BOLD)))
              .append(text("on Adventure!"))
            ))
            .build();

        assertEquals(
          text()
            .content("Hello World! What a ")
            .style(baseStyle)
            .append(text(c -> c.content("beautiful day ")
              .color(NamedTextColor.BLUE)
              .append(text("to create ", style(TextDecoration.ITALIC)))
              .append(text("a PR on Adventure!"))))
            .build(),
          component.compact());
      })
    );
  }

  @Test
  void testCompactingNestedComponentsInterruptedByOthers() {
    final Style baseStyle = style(NamedTextColor.RED, TextDecoration.BOLD, TextDecoration.OBFUSCATED);
    final Component input = translatable()
      .key("some.language.key")
      .style(baseStyle)
      .append(
        text(c -> c.content("Hello World! ")
          .append(text("What a ", style(TextDecoration.BOLD)))
          .append(text("beautiful ", style(TextDecoration.OBFUSCATED)))
          .append(translatable(t -> t.key("unit.day")
            .append(text(" to create ")
              .append(text("a PR on Adventure!")))))
        ))
      .build();

    assertEquals(translatable("some.language.key", baseStyle)
      .append(text("Hello World! What a beautiful ")
        .append(translatable("unit.day")
          .append(text(" to create a PR on Adventure!")))), input.compact());
  }

  @TestFactory
  Stream<DynamicTest> testCompactWithEmptyComponentsInHierarchy() {
    return Stream.of(
      dynamicTest("1", () -> {
        final Component component = text().content("Hello ").append(text().append(text("World!"))).build();
        assertEquals(text("Hello World!"), component.compact());
      }),
      dynamicTest("2", () -> {
        final Component component = text()
          .append(text().content("Hello ").append(text().append(text("World!"))))
          .build();
        assertEquals(text("Hello World!"), component.compact());
      }),
      dynamicTest("3", () -> {
        final Component component = text()
          .append(text()
            .append(text("Hello ")
              .append(text()
                .append(text("World!")))))
          .build();
        assertEquals(text("Hello World!"), component.compact());
      }),
      dynamicTest("4", () -> {
        final Component component = text()
          .append(text("Hello "))
          .append(text("World!"))
          .build();
        assertEquals(text("Hello World!"), component.compact());
      }),
      dynamicTest("5", () -> {
        final Component component = text()
          .append(text()
            .append(text("Hello ", style().font(key("alt")).build()))
            .append(text("World!", style().font(key("uniform")).build()))
          )
          .build();

        assertEquals(text()
            .append(text("Hello ", style().font(key("alt")).build()))
            .append(text("World!", style().font(key("uniform")).build()))
            .build(),
          component.compact());
      })
    );
  }
}
