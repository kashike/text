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
package net.kyori.adventure.text.serializer.plain;

import java.util.function.Function;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
final class PlainComponentSerializerImpl {
  @Deprecated static final PlainComponentSerializer INSTANCE = new PlainComponentSerializer();

  private PlainComponentSerializerImpl() {
  }

  @Deprecated
  static PlainTextComponentSerializer createRealSerializerFromLegacyFunctions(
    final @Nullable Function<KeybindComponent, String> keybind,
    final @Nullable Function<TranslatableComponent, String> translatable
  ) {
    // if both legacy functions are null we can simply use the standard plain-text serializer, since there is nothing special we need to do
    if(keybind == null && translatable == null) {
      return PlainTextComponentSerializer.plainText();
    }
    final ComponentFlattener.Builder builder = ComponentFlattener.basic().toBuilder();
    if(keybind != null) builder.mapper(KeybindComponent.class, keybind);
    if(translatable != null) builder.mapper(TranslatableComponent.class, translatable);
    return PlainTextComponentSerializer.builder().flattener(builder.build()).build();
  }

  @Deprecated
  static final class BuilderImpl implements PlainComponentSerializer.Builder {
    private final PlainTextComponentSerializer.Builder builder = PlainTextComponentSerializer.builder();

    @Deprecated
    BuilderImpl() {
    }

    @Deprecated
    BuilderImpl(final PlainComponentSerializer serializer) {
      this.builder.flattener(((PlainTextComponentSerializerImpl) serializer.serializer).flattener);
    }

    @Override
    public PlainComponentSerializer.@NonNull Builder flattener(final @NonNull ComponentFlattener flattener) {
      this.builder.flattener(flattener);
      return this;
    }

    @Override
    public @NonNull PlainComponentSerializer build() {
      return new PlainComponentSerializer(this.builder.build());
    }
  }
}
