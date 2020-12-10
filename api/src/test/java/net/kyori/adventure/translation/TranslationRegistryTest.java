/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
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
package net.kyori.adventure.translation;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TranslationRegistryTest {
  static final TranslationRegistry REGISTRY = TranslationRegistry.create(Key.key("adventure", "test"));
  static final TranslatableComponentRenderer<Locale> RENDERER = TranslatableComponentRenderer.usingTranslationSource(REGISTRY);

  @BeforeAll
  static void testRegister() {
    REGISTRY.register("what", Locale.CANADA, new MessageFormat("A what?", Locale.CANADA));

    final ResourceBundle bundle = ResourceBundle.getBundle("adventure-test", Locale.US, UTF8ResourceBundleControl.get());
    REGISTRY.registerAll(Locale.US, bundle, true);
  }

  @Test
  void testRegister_duplicate() {
    assertThrows(IllegalArgumentException.class, () -> REGISTRY.register("test", Locale.US, new MessageFormat("Another test.")));
  }

  @Test
  void testTranslate() {
    final MessageFormat expected = new MessageFormat("A what?", Locale.CANADA);
    assertEquals(expected, REGISTRY.translate("what", Locale.CANADA)); // "en_CA"
    assertNull(REGISTRY.translate("what", Locale.ENGLISH)); // "en"
    assertNull(REGISTRY.translate("what", Locale.US)); // "en_US"
  }

  @Test
  void testTranslateDelegatingDown() {
    final MessageFormat expected = new MessageFormat("This is a test.", Locale.US);
    assertEquals(expected, REGISTRY.translate("test", Locale.CANADA)); // "en_CA"
    assertEquals(expected, REGISTRY.translate("test", Locale.ENGLISH)); // "en"
    assertEquals(expected, REGISTRY.translate("test", Locale.US));
  }

  @Test
  void testTranslate_escapeQuotes() {
    assertEquals(new MessageFormat("{0} and ''{1}'' are cats.", Locale.US), REGISTRY.translate("cats", Locale.US));
  }

  @Test
  void testTranslate_utf8() {
    assertEquals(new MessageFormat("☃", Locale.US), REGISTRY.translate("snowperson", Locale.US));
  }

  @Test
  void testRender_simple() {
    assertEquals(
      Component.text("This is a test.", NamedTextColor.YELLOW),
      RENDERER.render(
        Component.translatableBuilder()
          .key("test")
          .color(NamedTextColor.YELLOW)
          .build(),
        Locale.US
      )
    );
  }

  @Test
  void testRender_complex() {
    assertEquals(
      Component.textBuilder().content("")
        .color(NamedTextColor.YELLOW)
        .append(Component.text("kashike"))
        .append(Component.text(" and '"))
        .append(Component.text("lucko"))
        .append(Component.text("' are cats."))
        .build(),
      RENDERER.render(
        Component.translatableBuilder()
          .key("cats")
          .args(
            Component.text("kashike"),
            Component.text("lucko")
          )
          .color(NamedTextColor.YELLOW)
          .build(),
        Locale.US
      )
    );
  }

  @Test
  void testRender_veryComplex() {
    assertEquals(
      Component.textBuilder().content("")
        .color(NamedTextColor.YELLOW)
        .append(
          Component.text("This is a test.")
            .append(Component.text("I promise."))
        )
        .append(
          Component.text("")
            .append(Component.text("kashike"))
            .append(Component.text(" and '"))
            .append(Component.text("lucko"))
            .append(Component.text("' are cats."))
            .append(Component.space())
            .append(Component.text("Meow!"))
            .hoverEvent(HoverEvent.showText(Component.text("This is a test.")))
        )
        .build(),
      RENDERER.render(
        Component.textBuilder()
          .content("")
          .append(
            Component.translatable("test")
              .append(Component.text("I promise."))
          )
          .append(
            Component.translatableBuilder()
              .key("cats")
              .args(
                Component.text("kashike"),
                Component.text("lucko")
              )
              .hoverEvent(HoverEvent.showText(Component.translatable("test")))
              .append(Component.space())
              .append(Component.text("Meow!"))
              .build()
          )
          .color(NamedTextColor.YELLOW)
          .build(),
        Locale.US
      )
    );
  }

  @Test
  void testUnknownTranslatableWithKnownArgsAndChildren() {
    assertEquals(
      Component.translatable("some.unknown.key")
        .args(
          Component.text("")
            .append(Component.text("kashike"))
            .append(Component.text(" and '"))
            .append(Component.text("lucko"))
            .append(Component.text("' are cats."))
        )
        .append(Component.text("This is a test.")),
      RENDERER.render(
        Component.translatable("some.unknown.key")
          .args(ImmutableList.of(
            Component.translatable("cats")
              .args(
                Component.text("kashike"),
                Component.text("lucko")
              )
          ))
          .children(ImmutableList.of(
            Component.translatable("test")
          )),
        Locale.US
      )
    );
  }

  @Test
  void testEquality() {
    new EqualsTester()
      .addEqualityGroup(REGISTRY)
      .addEqualityGroup(TranslationRegistry.create(Key.key("adventure", "test_2")))
      .testEquals();
  }

  @Test
  @AfterAll
  static void testUnregister() {
    REGISTRY.unregister("test");
    assertNull(REGISTRY.translate("test", Locale.US));
  }
}
