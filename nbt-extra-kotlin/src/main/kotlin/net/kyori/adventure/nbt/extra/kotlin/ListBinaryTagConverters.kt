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

import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.ByteArrayBinaryTag
import net.kyori.adventure.nbt.ByteBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.nbt.IntArrayBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
import net.kyori.adventure.nbt.LongArrayBinaryTag
import net.kyori.adventure.nbt.LongBinaryTag
import net.kyori.adventure.nbt.ShortBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

public fun <T : BinaryTag> List<T>.toBinaryTag(): ListBinaryTag = ListBinaryTag.from(this)

@JvmName("toBinaryTagByte")
public fun List<Byte>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.BYTE, map(ByteBinaryTag::of))

@JvmName("toBinaryTagShort")
public fun List<Short>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.SHORT, map(ShortBinaryTag::of))

@JvmName("toBinaryTagInt")
public fun List<Int>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.INT, map(IntBinaryTag::of))

@JvmName("toBinaryTagLong")
public fun List<Long>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.LONG, map(LongBinaryTag::of))

@JvmName("toBinaryTagFloat")
public fun List<Float>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.FLOAT, map(FloatBinaryTag::of))

@JvmName("toBinaryTagDouble")
public fun List<Double>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.DOUBLE, map(DoubleBinaryTag::of))

@JvmName("toBinaryTagByteArray")
public fun List<ByteArray>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.BYTE_ARRAY, map(ByteArrayBinaryTag::of))

@JvmName("toBinaryTagIntArray")
public fun List<IntArray>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.INT_ARRAY, map(IntArrayBinaryTag::of))

@JvmName("toBinaryTagLongArray")
public fun List<LongArray>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.LONG_ARRAY, map(LongArrayBinaryTag::of))

@JvmName("toBinaryTagString")
public fun List<String>.toBinaryTag(): ListBinaryTag = ListBinaryTag.of(BinaryTagTypes.STRING, map(StringBinaryTag::of))
