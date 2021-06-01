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
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.CompoundTagSetter

public operator fun <R> CompoundTagSetter<R>.set(key: String, tag: BinaryTag): R = put(key, tag)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Boolean): R = putBoolean(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Byte): R = putByte(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Short): R = putShort(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Int): R = putInt(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Long): R = putLong(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Float): R = putFloat(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: Double): R = putDouble(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: ByteArray): R = putByteArray(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: IntArray): R = putIntArray(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: LongArray): R = putLongArray(key, value)

public operator fun <R> CompoundTagSetter<R>.set(key: String, value: String): R = putString(key, value)

public operator fun <R> CompoundTagSetter<R>.plus(tag: CompoundBinaryTag): R = put(tag)

public operator fun CompoundBinaryTag.plusAssign(tag: CompoundBinaryTag) {
  put(tag)
}

public operator fun <R> CompoundTagSetter<R>.minus(key: String): R = remove(key)

public operator fun CompoundBinaryTag.minusAssign(key: String) {
  remove(key)
}
