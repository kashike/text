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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class ComponentCompacting {
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();

  private ComponentCompacting() {
  }

  static @NonNull Component optimize(final AbstractComponent component, final @Nullable Style parentStyle) {
    Component optimized = component.children(Collections.emptyList());

    if(parentStyle != null) {
      optimized = optimized.style(simplifyStyle(component.style(), parentStyle));
    }

    // propagate the parent style context to children
    // by merging this component's style into the parent style
    Style childParentStyle = optimized.style();
    if(parentStyle != null) {
      childParentStyle = parentStyle.merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
    }

    // optimize all children
    final List<Component> childrenToAppend = new ArrayList<>(component.children.size());
    for(int i = 0; i < component.children.size(); ++i) {
      childrenToAppend.add(optimize((AbstractComponent) component.children.get(i), childParentStyle));
    }

    // try to merge children into this parent component
    for(final ListIterator<Component> it = childrenToAppend.listIterator(); it.hasNext();) {
      final Component child = it.next();
      final Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);

      if(optimized instanceof TextComponent && child instanceof TextComponent && Objects.equals(childStyle, childParentStyle)) {
        // merge child components into the parent if they are a text component with the same effective style
        // in context of their parent style
        optimized = concatContentWithFirstStyle((TextComponent) optimized, (TextComponent) child);
        it.remove();

        // if the merged child had any children, retain them
        child.children().forEach(it::add);
      } else {
        // this child can't be merged into the parent, so all children from now on must remain children
        break;
      }
    }

    // try to concatenate any further children with their neighbor
    // until no further joining is possible
    for(int i = 0; i + 1 < childrenToAppend.size();) {
      final Component child = childrenToAppend.get(i);
      final Component neighbor = childrenToAppend.get(i + 1);

      // calculate the children's styles in context of their parent style
      final Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
      final Style neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);

      if(child instanceof TextComponent && neighbor instanceof TextComponent && childStyle.equals(neighborStyle)) {
        final Component combined = concatContentWithFirstStyle((TextComponent) child, (TextComponent) neighbor);

        // replace the child and its neighbor with the single, combined component
        childrenToAppend.set(i, combined);
        childrenToAppend.remove(i + 1);

        // don't increment the index -
        // we want to try and optimize this combined component even further
      } else {
        i++;
      }
    }

    return optimized.children(childrenToAppend);
  }

  // todo(kashike): extract
  /**
   * Simplify the provided style to remove any information that is redundant.
   *
   * @param style style to simplify
   * @param parentStyle parent to compare against
   * @return a new, simplified style
   */
  private static @NonNull Style simplifyStyle(final @NonNull Style style, final @NonNull Style parentStyle) {
    final Style.Builder builder = style.toBuilder();

    if(Objects.equals(style.font(), parentStyle.font())) {
      builder.font(null);
    }

    if(Objects.equals(style.color(), parentStyle.color())) {
      builder.color(null);
    }

    for(int i = 0, length = DECORATIONS.length; i < length; i++) {
      final TextDecoration decoration = DECORATIONS[i];
      if(style.decoration(decoration) == parentStyle.decoration(decoration)) {
        builder.decoration(decoration, TextDecoration.State.NOT_SET);
      }
    }

    if(Objects.equals(style.clickEvent(), parentStyle.clickEvent())) {
      builder.clickEvent(null);
    }

    if(Objects.equals(style.hoverEvent(), parentStyle.hoverEvent())) {
      builder.hoverEvent(null);
    }

    if(Objects.equals(style.insertion(), parentStyle.insertion())) {
      builder.insertion(null);
    }

    return builder.build();
  }

  private static TextComponent concatContentWithFirstStyle(final TextComponent one, final TextComponent two) {
    return Component.text(one.content() + two.content(), one.style());
  }
}
