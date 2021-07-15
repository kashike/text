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

import net.kyori.adventure.util.RGBLike

/**
 * The [RGBLike.red] component.
 *
 * Allows for destructuring into `(r, g, b)`.
 *
 * @return the [RGBLike.red] component
 * @since 4.6.0
 */
public operator fun RGBLike.component1(): Int = this.red()

/**
 * The [RGBLike.green] component.
 *
 * Allows for destructuring into `(r, g, b)`.
 *
 * @return the [RGBLike.green] component
 * @since 4.6.0
 */
public operator fun RGBLike.component2(): Int = this.green()

/**
 * The [RGBLike.blue] component.
 *
 * Allows for destructuring into `(r, g, b)`.
 *
 * @return the [RGBLike.blue] component
 * @since 4.6.0
 */
public operator fun RGBLike.component3(): Int = this.blue()
