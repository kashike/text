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
package net.kyori.adventure.extra.kotlin

import net.kyori.adventure.text.BlockNBTComponent

/**
 * The X component of this world position.
 *
 * Allows for destructuring into `(x, y, z)`.
 *
 * @return the X component
 * @since 4.8.0
 */
public operator fun BlockNBTComponent.WorldPos.component1(): BlockNBTComponent.WorldPos.Coordinate = x()

/**
 * The Y component of this world position.
 *
 * Allows for destructuring into `(x, y, z)`.
 *
 * @return the Y component
 * @since 4.8.0
 */
public operator fun BlockNBTComponent.WorldPos.component2(): BlockNBTComponent.WorldPos.Coordinate = y()

/**
 * The Z component of this world position.
 *
 * Allows for destructuring into `(x, y, z)`.
 *
 * @return the Z component
 * @since 4.8.0
 */
public operator fun BlockNBTComponent.WorldPos.component3(): BlockNBTComponent.WorldPos.Coordinate = z()
