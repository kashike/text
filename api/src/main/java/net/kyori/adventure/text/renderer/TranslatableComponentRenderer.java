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
package net.kyori.adventure.text.renderer;

import java.text.AttributedCharacterIterator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.NBTComponentBuilder;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.translation.Translator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A component renderer that does server-side translation rendering.
 *
 * @param <C> the context type, usually {@link java.util.Locale}.
 * @since 4.0.0
 */
public abstract class TranslatableComponentRenderer<C> extends AbstractComponentRenderer<C> {
  private static final Set<Style.Merge> MERGES = Style.Merge.of(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT);

  /**
   * Creates a {@link TranslatableComponentRenderer} using the {@link Translator} to translate.
   *
   * @param source the translation source
   * @return the renderer
   * @since 4.0.0
   */
  public static @NonNull TranslatableComponentRenderer<Locale> usingTranslationSource(final @NonNull Translator source) {
    requireNonNull(source, "source");
    return new TranslatableComponentRenderer<Locale>() {
      @Override
      protected @Nullable MessageFormat translate(final @NonNull String key, final @NonNull Locale context) {
        return source.translate(key, context);
      }
    };
  }

  /**
   * Gets a message format from a key and context.
   *
   * @param key a translation key
   * @param context a context
   * @return a message format or {@code null} to skip translation
   */
  protected abstract @Nullable MessageFormat translate(final @NonNull String key, final @NonNull C context);

  @Override
  protected @NonNull Component renderBlockNbt(final @NonNull BlockNBTComponent component, final @NonNull C context) {
    final BlockNBTComponent.Builder builder = nbt(Component.blockNBTBuilder(), component)
      .pos(component.pos());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderEntityNbt(final @NonNull EntityNBTComponent component, final @NonNull C context) {
    final EntityNBTComponent.Builder builder = nbt(Component.entityNBTBuilder(), component)
      .selector(component.selector());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderStorageNbt(final @NonNull StorageNBTComponent component, final @NonNull C context) {
    final StorageNBTComponent.Builder builder = nbt(Component.storageNBTBuilder(), component)
      .storage(component.storage());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  protected static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(final B builder, final C oldComponent) {
    return builder
      .nbtPath(oldComponent.nbtPath())
      .interpret(oldComponent.interpret());
  }

  @Override
  protected @NonNull Component renderKeybind(final @NonNull KeybindComponent component, final @NonNull C context) {
    final KeybindComponent.Builder builder = Component.keybindBuilder().keybind(component.keybind());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderScore(final @NonNull ScoreComponent component, final @NonNull C context) {
    final ScoreComponent.Builder builder = Component.scoreBuilder()
      .name(component.name())
      .objective(component.objective())
      .value(component.value());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderSelector(final @NonNull SelectorComponent component, final @NonNull C context) {
    final SelectorComponent.Builder builder = Component.selectorBuilder().pattern(component.pattern());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderText(final @NonNull TextComponent component, final @NonNull C context) {
    final TextComponent.Builder builder = Component.textBuilder().content(component.content());
    return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
  }

  @Override
  protected @NonNull Component renderTranslatable(final @NonNull TranslatableComponent component, final @NonNull C context) {
    final @Nullable MessageFormat format = this.translate(component.key(), context);
    if(format == null) {
      // we don't have a translation for this component, but the arguments or children
      // of this component might need additional rendering

      final TranslatableComponent.Builder builder = Component.translatableBuilder()
        .key(component.key());
      if(!component.args().isEmpty()) {
        final List<Component> args = new ArrayList<>(component.args());
        for(int i = 0, size = args.size(); i < size; i++) {
          args.set(i, this.render(args.get(i), context));
        }
        builder.args(args);
      }
      return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    final List<Component> args = component.args();

    final TextComponent.Builder builder = Component.textBuilder();
    this.mergeStyle(component, builder, context);

    // no arguments makes this render very simple
    if(args.isEmpty()) {
      builder.content(format.format(null, new StringBuffer(), null).toString());
      return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }

    final Object[] nulls = new Object[args.size()];
    final StringBuffer sb = format.format(nulls, new StringBuffer(), null);
    final AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);

    while(it.getIndex() < it.getEndIndex()) {
      final int end = it.getRunLimit();
      final Integer index = (Integer) it.getAttribute(MessageFormat.Field.ARGUMENT);
      if(index != null) {
        builder.append(this.render(args.get(index), context));
      } else {
        builder.append(Component.text(sb.substring(it.getIndex(), end)));
      }
      it.setIndex(end);
    }

    return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
  }

  protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(final Component component, final B builder, final C context) {
    this.mergeStyle(component, builder, context);
    return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
  }

  protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(final List<Component> children, final B builder, final C context) {
    if(!children.isEmpty()) {
      children.forEach(child -> builder.append(this.render(child, context)));
    }
    return builder.build();
  }

  protected <B extends ComponentBuilder<?, ?>> void mergeStyle(final Component component, final B builder, final C context) {
    builder.mergeStyle(component, MERGES);
    builder.clickEvent(component.clickEvent());
    final @Nullable HoverEvent<?> hoverEvent = component.hoverEvent();
    if(hoverEvent != null) {
      builder.hoverEvent(hoverEvent.withRenderedValue(this, context));
    }
  }
}
