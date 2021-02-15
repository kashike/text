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

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

/**
 * An implementation of the pixel width source which handles serialization with a set of functions
 * for resolving all components except {@link TextComponent}s.
 *
 * @param <CX> a context (player, server, locale)
 * @since 4.5.0
 */

class PixelWidthSourceImpl<CX> implements PixelWidthSource<CX> {

  private final Set<ComponentResolver<KeybindComponent, CX>> keybindResolvers = new HashSet<>();
  private final Set<ComponentResolver<SelectorComponent, CX>> selectorResolvers = new HashSet<>();
  private final Set<ComponentResolver<ScoreComponent, CX>> scoreResolvers = new HashSet<>();
  private final Set<ComponentResolver<BlockNBTComponent, CX>> blockNBTResolvers = new HashSet<>();
  private final Set<ComponentResolver<EntityNBTComponent, CX>> entityNBTResolvers = new HashSet<>();
  private final Set<ComponentResolver<StorageNBTComponent, CX>> storageNBTResolvers = new HashSet<>();
  private final Set<ComponentResolver<TranslatableComponent, CX>> translatableResolvers = new HashSet<>();

  private final PixelWidthRenderer renderer = new PixelWidthRenderer();

  private final Function<CX, CharacterWidthFunction> function;
  private final Function<CX, Locale> localeFunction;

  /**
   * Get the pixel width of the given character in the Minecraft font, excluding the space between
   * characters and drop shadow. Handles alphanumerics and most common english punctuation.
   */
  static final CharacterWidthFunction DEFAULT_FONT_WIDTH = (c, style) -> {
    int i;
    if(Character.isUpperCase(c)) {
      i = c == 'I' ? 3 : 5;
    } else if(Character.isDigit(c)) {
      i = 5;
    } else if(Character.isLowerCase(c)) {
      switch (c) {
        case 'i':
          i = 1;
          break;

        case 'l':
          i = 2;
          break;

        case 't':
          i = 3;
          break;

        case 'f':
        case 'k':
          i = 4;
          break;

        default:
          i = 5;
          break;
      }
    } else {
      switch (c) {
        case '!':
        case '.':
        case ',':
        case ';':
        case ':':
        case '|':
          i = 1;
          break;

        case '\'':
          i = 2;
          break;

        case '[':
        case ']':
        case ' ':
          i = 3;
          break;

        case '*':
        case '(':
        case ')':
        case '{':
        case '}':
        case '<':
        case '>':
          i = 4;
          break;

        case '@':
          i = 6;
          break;

        default:
          i = 5;
          break;
      }
    }

    if(style.hasDecoration(TextDecoration.BOLD)) {
      i++;
    }

    return i;
  };

  /**
   * Creates a pixel width source with a function used for getting a {@link CharacterWidthFunction}.
   *
   * <p>Any {@link CharacterWidthFunction} returned by the function should accept at least all
   * english alphanumerics and most punctuation and handle {@link TextDecoration#BOLD} in the style.
   * See {@link PixelWidthSourceImpl#DEFAULT_FONT_WIDTH} for an example.</p>
   *
   * @param function a function that can provide a {@link CharacterWidthFunction} given a context
   * @since 4.5.0
   */
  PixelWidthSourceImpl(final @NonNull Function<CX, CharacterWidthFunction> function, final @NonNull Function<CX, Locale> localeFunction) {
    this.function = function;
    this.localeFunction = localeFunction;
  }

  @Override
  public int width(final @NonNull Component component, final @NonNull CX context) {
    final Map<String, Style> map = this.serialize(component, context);
    final AtomicInteger length = new AtomicInteger(0);

    map.forEach((string, style) -> string.chars().forEach(c -> length.getAndAdd(this.width((char) c, style, context))));

    return length.get();
  }

  @Override
  public int width(final @NonNull String string, final @NonNull Style style, final @NonNull CX context) {
    int length = 0;
    for(final char c : string.toCharArray()) length += this.width(c, style, context);
    return length;
  }

  @Override
  public int width(final char c, final @NonNull Style style, final @NonNull CX context) {
    return this.function.apply(context).applyAsInt(c, style);
  }

  @SuppressWarnings(value = "unchecked")
  @Override
  public <CO extends Component> void addResolver(final @NonNull Class<CO> resolveFor, final @NonNull ComponentResolver<CO, CX> resolver) {
    if(resolveFor.isAssignableFrom(TextComponent.class)) {
      throw new UnsupportedOperationException("Can not add custom resolver for TextComponents");
    } else if(resolveFor.isAssignableFrom(TranslatableComponent.class)) {
      this.translatableResolvers.add((ComponentResolver<TranslatableComponent, CX>) resolver);
      return;
    } else if(resolveFor.isAssignableFrom(KeybindComponent.class)) {
      this.keybindResolvers.add((ComponentResolver<KeybindComponent, CX>) resolver);
      return;
    } else if(resolveFor.isAssignableFrom(ScoreComponent.class)) {
      this.scoreResolvers.add((ComponentResolver<ScoreComponent, CX>) resolver);
      return;
    } else if(resolveFor.isAssignableFrom(SelectorComponent.class)) {
      this.selectorResolvers.add((ComponentResolver<SelectorComponent, CX>) resolver);
      return;
    } else if(resolveFor.isAssignableFrom(NBTComponent.class)) {
      if(resolveFor.isAssignableFrom(BlockNBTComponent.class)) {
        this.blockNBTResolvers.add((ComponentResolver<BlockNBTComponent, CX>) resolver);
        return;
      } else if(resolveFor.isAssignableFrom(EntityNBTComponent.class)) {
        this.entityNBTResolvers.add((ComponentResolver<EntityNBTComponent, CX>) resolver);
        return;
      } else if(resolveFor.isAssignableFrom(StorageNBTComponent.class)) {
        this.storageNBTResolvers.add((ComponentResolver<StorageNBTComponent, CX>) resolver);
        return;
      }
    }

    throw new UnsupportedOperationException("Invalid Component");
  }

  /**
   * Serializes using the renderer.
   *
   * @param component the component
   * @since 4.5.0
   */
  private Map<String, Style> serialize(final @NonNull Component component, final @NonNull CX context) {

    final TextComponent rendered = this.renderer.render(component, context);

    return this.flattenComponents(rendered, new HashMap<>());
  }

  /**
   * Flattens a {@link TextComponent} and <em>all</em> it's {@link TextComponent} children,
   * wherever they are, into a map where the content and style of each component is linked together.
   */
  private Map<String, Style> flattenComponents(final @NonNull TextComponent parent, final @NonNull Map<String, Style> map) {
    //By this point, we know that all components in this component tree are TextComponents,
    //so some casting is acceptable
    map.put(parent.content(), parent.style());
    for(final Component child : parent.children()) {
      map.put(((TextComponent) child).content(), child.style());
      map.putAll(this.flattenComponents((TextComponent) child, map));
    }

    return map;
  }

  /**
   * Always returns {@link TextComponent}s.
   */
  private class PixelWidthRenderer extends TranslatableComponentRenderer<CX> {

    @Override
    public @NonNull TextComponent render(final @NonNull Component component, final @NonNull CX context) {
      return this.mergeStylesDownwards(super.render(component, context));
    }

    @Override
    protected @Nullable MessageFormat translate(final @NonNull String key, final @NonNull CX context) {
      return GlobalTranslator.get().translate(key, PixelWidthSourceImpl.this.localeFunction.apply(context));
    }

    @Override
    protected @NonNull TextComponent renderBlockNbt(final @NonNull BlockNBTComponent component, final @NonNull CX context) {
      final BlockNBTComponent component0 = (BlockNBTComponent) super.renderBlockNbt(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.blockNBTResolvers, component0, context);

      return rendered != null ? rendered : text(component.pos().asString(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderEntityNbt(final @NonNull EntityNBTComponent component, final @NonNull CX context) {
      final EntityNBTComponent component0 = (EntityNBTComponent) super.renderEntityNbt(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.entityNBTResolvers, component0, context);

      return rendered != null ? rendered : text(component.selector(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderStorageNbt(final @NonNull StorageNBTComponent component, final @NonNull CX context) {
      final StorageNBTComponent component0 = (StorageNBTComponent) super.renderStorageNbt(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.storageNBTResolvers, component0, context);

      return rendered != null ? rendered : text(component.storage().asString(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderKeybind(final @NonNull KeybindComponent component, final @NonNull CX context) {
      final KeybindComponent component0 = (KeybindComponent) super.renderKeybind(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.keybindResolvers, component0, context);

      return rendered != null ? rendered : text(component.keybind(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderScore(final @NonNull ScoreComponent component, final @NonNull CX context) {
      final ScoreComponent component0 = (ScoreComponent) super.renderScore(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.scoreResolvers, component, context);

      return rendered != null ? rendered : text(component.name() + "_" + component.objective() + "_" + component.value(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderSelector(final @NonNull SelectorComponent component, final @NonNull CX context) {
      final SelectorComponent component0 = (SelectorComponent) super.renderSelector(component, context);

      final TextComponent rendered = this.renderUsingResolver(PixelWidthSourceImpl.this.selectorResolvers, component, context);

      return rendered != null ? rendered : text(component.pattern(), component0.style()).children(component0.children());
    }

    @Override
    protected @NonNull TextComponent renderText(final @NonNull TextComponent component, final @NonNull CX context) {
      return (TextComponent) super.renderText(component, context); //easy money
    }

    @Override
    protected @NonNull TextComponent renderTranslatable(final @NonNull TranslatableComponent component, final @NonNull CX context) {
      Component component0 = super.renderTranslatable(component, context);

      if(!(component0 instanceof TextComponent)) component0 = this.renderUsingResolver(PixelWidthSourceImpl.this.translatableResolvers, component, context);

      return component0 != null ? (TextComponent) component0 : text(component.key(), component.style()).children(component.children());
    }

    //null -> unsuccessful
    private <CO extends Component> @Nullable TextComponent renderUsingResolver(final @NonNull Set<ComponentResolver<CO, CX>> resolvers, final @NonNull CO component, final @NonNull CX context) {
      TextComponent result = null;
      for(final ComponentResolver<CO, CX> resolver : resolvers) {
        final @Nullable TextComponent rendered = resolver.resolve(component, context);
        if(rendered != null) result = rendered;
      }

      return result;
    }

    private @NonNull TextComponent mergeStylesDownwards(final @NonNull Component parent) {
      final TextComponent.Builder builder = text();

      builder.append(parent);
      for(final Component child : parent.children()) {
        final Style merged = child.style().merge(parent.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET, Style.Merge.all());
        builder.append(this.mergeStylesDownwards(child.style(merged)));
      }

      return builder.build();
    }
  }
}
