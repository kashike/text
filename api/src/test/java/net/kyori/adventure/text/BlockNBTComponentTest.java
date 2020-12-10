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

import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockNBTComponentTest extends AbstractNBTComponentTest<BlockNBTComponent, BlockNBTComponent.Builder> {
  @Override
  BlockNBTComponent.Builder builder() {
    return Component.blockNBTBuilder().nbtPath("abc").absoluteWorldPos(1, 2, 3);
  }

  @Test
  void testOf() {
    final BlockNBTComponent component = Component.blockNBT("abc", BlockNBTComponent.LocalPos.of(1d, 2d, 3d));
    assertEquals("abc", component.nbtPath());
    assertEquals(BlockNBTComponent.LocalPos.of(1d, 2d, 3d), component.pos());
    assertNull(component.color());
    TextAssertions.assertDecorations(component, ImmutableSet.of(), ImmutableSet.of());
  }

  @Test
  void testPos() {
    final BlockNBTComponent c0 = Component.blockNBT("abc", BlockNBTComponent.LocalPos.of(1d, 2d, 3d));
    final BlockNBTComponent c1 = c0.pos(BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.absolute(1), BlockNBTComponent.WorldPos.Coordinate.relative(2), BlockNBTComponent.WorldPos.Coordinate.absolute(3)));
    assertEquals(BlockNBTComponent.LocalPos.of(1d, 2d, 3d), c0.pos());
    assertEquals(BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.absolute(1), BlockNBTComponent.WorldPos.Coordinate.relative(2), BlockNBTComponent.WorldPos.Coordinate.absolute(3)), c1.pos());
    assertEquals("abc", c1.nbtPath());

    assertTrue(c0.pos() instanceof BlockNBTComponent.LocalPos);
    assertEquals(1, ((BlockNBTComponent.LocalPos) c0.pos()).left());
    assertEquals(2, ((BlockNBTComponent.LocalPos) c0.pos()).up());
    assertEquals(3, ((BlockNBTComponent.LocalPos) c0.pos()).forwards());

    assertTrue(c1.pos() instanceof BlockNBTComponent.WorldPos);
    assertEquals(BlockNBTComponent.WorldPos.Coordinate.Type.ABSOLUTE, ((BlockNBTComponent.WorldPos) c1.pos()).x().type());
    assertEquals(BlockNBTComponent.WorldPos.Coordinate.Type.RELATIVE, ((BlockNBTComponent.WorldPos) c1.pos()).y().type());
    assertEquals(BlockNBTComponent.WorldPos.Coordinate.Type.ABSOLUTE, ((BlockNBTComponent.WorldPos) c1.pos()).z().type());
    assertEquals(1, ((BlockNBTComponent.WorldPos) c1.pos()).x().value());
    assertEquals(2, ((BlockNBTComponent.WorldPos) c1.pos()).y().value());
    assertEquals(3, ((BlockNBTComponent.WorldPos) c1.pos()).z().value());
  }

  @Test
  void testPosEquality() {
    new EqualsTester()
      .addEqualityGroup(BlockNBTComponent.LocalPos.of(1d, 2d, 3d))
      .addEqualityGroup(BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.absolute(1), BlockNBTComponent.WorldPos.Coordinate.absolute(2), BlockNBTComponent.WorldPos.Coordinate.absolute(3)))
      .testEquals();
  }

  @Test
  void testLocalPosNoDecimalParsing() {
    assertEquals(
      BlockNBTComponent.LocalPos.of(1.0d, 2.0d, 3.89d),
      BlockNBTComponent.Pos.fromString("^1 ^2 ^3.89")
    );
  }

  @Test
  void testLocalPosParsing() {
    assertEquals(
      BlockNBTComponent.LocalPos.of(1.23d, 2.0d, 3.89d),
      BlockNBTComponent.Pos.fromString("^1.23 ^2.0 ^3.89")
    );
  }

  @Test
  void testAbsoluteWorldPosParsing() {
    assertEquals(
      BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.absolute(4), BlockNBTComponent.WorldPos.Coordinate.absolute(5), BlockNBTComponent.WorldPos.Coordinate.absolute(6)),
      BlockNBTComponent.Pos.fromString("4 5 6")
    );
  }

  @Test
  void testRelativeWorldPosParsing() {
    assertEquals(
      BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.relative(7), BlockNBTComponent.WorldPos.Coordinate.relative(83), BlockNBTComponent.WorldPos.Coordinate.relative(900)),
      BlockNBTComponent.Pos.fromString("~7 ~83 ~900")
    );
  }

  @Test
  void testWorldPosParsing() {
    assertEquals(
      BlockNBTComponent.WorldPos.of(BlockNBTComponent.WorldPos.Coordinate.absolute(12), BlockNBTComponent.WorldPos.Coordinate.relative(3), BlockNBTComponent.WorldPos.Coordinate.absolute(1200)),
      BlockNBTComponent.Pos.fromString("12 ~3 1200")
    );
  }
}
