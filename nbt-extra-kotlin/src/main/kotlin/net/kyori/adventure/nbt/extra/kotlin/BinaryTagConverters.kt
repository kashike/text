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
package net.kyori.adventure.nbt.extra.kotlin

import net.kyori.adventure.nbt.ArrayBinaryTag
import net.kyori.adventure.nbt.ByteArrayBinaryTag
import net.kyori.adventure.nbt.ByteBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.nbt.IntArrayBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.LongArrayBinaryTag
import net.kyori.adventure.nbt.LongBinaryTag
import net.kyori.adventure.nbt.ShortBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

public fun Byte.toBinaryTag(): ByteBinaryTag = ByteBinaryTag.of(this)

public fun Short.toBinaryTag(): ShortBinaryTag = ShortBinaryTag.of(this)

public fun Int.toBinaryTag(): IntBinaryTag = IntBinaryTag.of(this)

public fun Long.toBinaryTag(): LongBinaryTag = LongBinaryTag.of(this)

public fun Float.toBinaryTag(): FloatBinaryTag = FloatBinaryTag.of(this)

public fun Double.toBinaryTag(): DoubleBinaryTag = DoubleBinaryTag.of(this)

public fun ByteArray.toBinaryTag(): ByteArrayBinaryTag = ByteArrayBinaryTag.of(*this)

public fun IntArray.toBinaryTag(): IntArrayBinaryTag = IntArrayBinaryTag.of(*this)

public fun LongArray.toBinaryTag(): LongArrayBinaryTag = LongArrayBinaryTag.of(*this)

public fun String.toBinaryTag(): StringBinaryTag = StringBinaryTag.of(this)
