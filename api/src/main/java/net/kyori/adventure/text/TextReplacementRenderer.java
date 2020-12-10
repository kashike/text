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
package net.kyori.adventure.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.util.IntFunction2;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A renderer performing a replacement on every {@link TextComponent} element of a component tree.
 */
final class TextReplacementRenderer implements ComponentRenderer<TextReplacementRenderer.State> {
  static final TextReplacementRenderer INSTANCE = new TextReplacementRenderer();

  private TextReplacementRenderer() {
  }

  @Override
  public @NonNull Component render(final @NonNull Component component, final @NonNull State state) {
    if(!state.running) return component;
    final boolean prevFirstMatch = state.firstMatch;
    state.firstMatch = true;

    final List<Component> oldChildren = component.children();
    final int oldChildrenSize = oldChildren.size();
    List<Component> children = null;
    Component modified = component;
    // replace the component itself
    if(component instanceof TextComponent) {
      final String content = ((TextComponent) component).content();
      final Matcher matcher = state.pattern.matcher(content);
      int replacedUntil = 0; // last index handled
      while(matcher.find()) {
        final PatternReplacementResult result = state.continuer.apply(++state.matchCount, state.replaceCount);
        if(result == PatternReplacementResult.CONTINUE) {
          // ignore this replacement
          continue;
        } else if(result == PatternReplacementResult.STOP) {
          // end replacement
          state.running = false;
          break;
        }

        if(matcher.start() == 0) {
          // if we're a full match, modify the component directly
          if(matcher.end() == content.length()) {
            final ComponentLike replacement = state.replacement.apply(matcher, Component.textBuilder().content(matcher.group())
              .style(component.style()));

            modified = replacement == null ? Component.empty() : replacement.asComponent();
          } else {
            // otherwise, work on a child of the root node
            modified = Component.text("", component.style());
            final ComponentLike child = state.replacement.apply(matcher, Component.textBuilder().content(matcher.group()));
            if(child != null) {
              if(children == null) {
                children = new ArrayList<>(oldChildrenSize + 1);
              }
              children.add(child.asComponent());
            }
          }
        } else {
          if(children == null) {
            children = new ArrayList<>(oldChildrenSize + 2);
          }
          if(state.firstMatch) {
            // truncate parent to content before match
            modified = ((TextComponent) component).content(content.substring(0, matcher.start()));
          } else if(replacedUntil < matcher.start()) {
            children.add(Component.text(content.substring(replacedUntil, matcher.start())));
          }
          final ComponentLike builder = state.replacement.apply(matcher, Component.textBuilder().content(matcher.group()));
          if(builder != null) {
            children.add(builder.asComponent());
          }
        }
        state.replaceCount++;
        state.firstMatch = false;
        replacedUntil = matcher.end();
      }
      if(replacedUntil < content.length()) {
        // append trailing content
        if(replacedUntil > 0) {
          if(children == null) {
            children = new ArrayList<>(oldChildrenSize);
          }
          children.add(Component.text(content.substring(replacedUntil)));
        }
        // otherwise, we haven't modified the component, so nothing to change
      }
    } else if(modified instanceof TranslatableComponent) { // get TranslatableComponent with() args
      final List<Component> args = ((TranslatableComponent) modified).args();
      List<Component> newArgs = null;
      for(int i = 0, size = args.size(); i < size; i++) {
        final Component original = args.get(i);
        final Component replaced = this.render(original, state);
        if(replaced != component) {
          if(newArgs == null) {
            newArgs = new ArrayList<>(size);
            if(i > 0) {
              newArgs.addAll(args.subList(0, i));
            }
          }
        }
        if(newArgs != null) {
          newArgs.add(replaced);
        }
      }
      if(newArgs != null) {
        modified = ((TranslatableComponent) modified).args(newArgs);
      }
    }
    // Only visit children if we're running
    if(state.running) {
      // hover event
      final HoverEvent<?> event = modified.style().hoverEvent();
      if(event != null) {
        final HoverEvent<?> rendered = event.withRenderedValue(this, state);
        if(event != rendered) {
          modified = modified.style(s -> s.hoverEvent(rendered));
        }
      }
      // Children
      boolean first = true;
      for(int i = 0; i < oldChildrenSize; i++) {
        final Component child = oldChildren.get(i);
        final Component replaced = this.render(child, state);
        if(replaced != child) {
          if(children == null) {
            children = new ArrayList<>(oldChildrenSize);
          }
          if(first) {
            children.addAll(oldChildren.subList(0, i));
          }
          first = false;
        }
        if(children != null) {
          children.add(replaced);
        }
      }
    } else {
      // we're not visiting children, re-add original children if necessary
      if(children != null) {
        children.addAll(oldChildren);
      }
    }

    state.firstMatch = prevFirstMatch;
    // Update the modified component with new children
    if(children != null) {
      return modified.children(children);
    }
    return modified;
  }

  static final class State {
    final Pattern pattern;
    final BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement;
    final IntFunction2<PatternReplacementResult> continuer;
    boolean running = true;
    int matchCount = 0;
    int replaceCount = 0;
    boolean firstMatch = true;

    State(final @NonNull Pattern pattern, final @NonNull BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement, final @NonNull IntFunction2<PatternReplacementResult> continuer) {
      this.pattern = pattern;
      this.replacement = replacement;
      this.continuer = continuer;
    }
  }
}
