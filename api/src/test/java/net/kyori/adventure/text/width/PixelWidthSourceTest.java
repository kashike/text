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
package net.kyori.adventure.text.width;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.score;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.Style.empty;
import static net.kyori.adventure.text.format.Style.style;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PixelWidthSourceTest {

  private static final TranslationRegistry DUMMY_REGISTRY = TranslationRegistry.create(Key.key("adventure-test", "width"));

  private final PixelWidthSource<DummyContext> defaultPixelWidth = PixelWidthSource.defaultPixelWidth(DummyContext::locale);
  private final DummyContext context = new DummyContext(Locale.US);

  private final Style bold = style(TextDecoration.BOLD);

  static{
    final Map<String, MessageFormat> translations = new HashMap<>();
    translations.put("dummy.welcome", new MessageFormat("Welcome {0}!"));
    translations.put("dummy.news", new MessageFormat("News: {0}"));
    translations.put("dummy.level", new MessageFormat("{0} just achieved level 99!"));
    translations.put("dummy.wow", new MessageFormat("wow"));

    DUMMY_REGISTRY.registerAll(Locale.US, translations);

    GlobalTranslator.get().addSource(DUMMY_REGISTRY);
  }

  @Test
  public void testDefaultSimpleWidth(){
    assertEquals(21, this.defaultPixelWidth.width("wowie", empty(), this.context));
    assertEquals(5, this.defaultPixelWidth.width(text(2), this.context));
    assertEquals(6, this.defaultPixelWidth.width('@', empty(), this.context));
    assertEquals(15, this.defaultPixelWidth.width(translatable("dummy.wow"), this.context));
  }

  @Test
  public void testDefaultWidthBold(){
    assertEquals(26, this.defaultPixelWidth.width(text("wowie", this.bold), this.context));
    assertEquals(7, this.defaultPixelWidth.width('@', this.bold, this.context));
  }

  @Test
  public void testWidthInheritedStyle(){
    assertEquals(33, this.defaultPixelWidth.width(text("wowie", this.bold).append(text('@')), this.context));
  }

  @Test
  public void testWidthChildrenAndArgs(){
    final Component component0 = text("kashike", this.bold); //29 + (1*7)
    final Component component1 = translatable("dummy.welcome", component0); //36 + 36
    final Component component2 = translatable("dummy.level", text("electroid", style(TextDecoration.ITALIC))).decoration(TextDecoration.BOLD, false); //96 + 36
    final Component component3 = translatable("dummy.news", this.bold, component2); //30 + 132

    final Component welcomePrompt = component1.append(component3);

    assertEquals(36, this.defaultPixelWidth.width(component0, this.context));
    assertEquals(72, this.defaultPixelWidth.width(component1, this.context)); //includes component
    assertEquals(132, this.defaultPixelWidth.width(component2, this.context));
    assertEquals(162, this.defaultPixelWidth.width(component3, this.context)); //includes component2
    assertEquals(234, this.defaultPixelWidth.width(welcomePrompt, this.context)); //includes all components
  }

  @Test
  public void testWidthCustomResolver(){
    this.defaultPixelWidth.addResolver(KeybindComponent.class, (co, d) -> {
      final String keybind = this.context.keybinds().get(co.keybind());
      if(keybind != null) return text(keybind);
      return null;
    });
    assertEquals(40, this.defaultPixelWidth.width(keybind("key.jump"), this.context));
  }

  @Test
  public void testWidthMultipleCustomResolvers(){
    this.defaultPixelWidth.addResolver(ScoreComponent.class, (co, d) -> {
      final String value = co.value();
      return value != null ? text(value) : null;
    });

    this.defaultPixelWidth.addResolver(ScoreComponent.class, (co, d) -> text(co.name()));

    assertEquals(12, this.defaultPixelWidth.width(score("zml", "dummy"), this.context));
    assertThat(this.defaultPixelWidth.width(score("luck", "dummy", "secrets"), this.context)).isAnyOf(33, 16);
  }

  @Test
  public void testAddTextResolver(){
    assertThrows(UnsupportedOperationException.class, () -> this.defaultPixelWidth.addResolver(TextComponent.class, (co, d) -> text("insert")));
  }

  @Test
  public void testWidthUsingCustomCharacterFunction(){
    final PixelWidthSource<DummyContext> custom = PixelWidthSource.withCustomCharacterFunction(d -> new CustomFontCharacterWidthFunction(), DummyContext::locale);
    assertEquals(17, custom.width(text("aA1 ").append(text("2", NamedTextColor.RED, TextDecoration.OBFUSCATED)), this.context));
  }

}
