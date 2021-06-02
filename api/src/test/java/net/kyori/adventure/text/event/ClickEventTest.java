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
package net.kyori.adventure.text.event;

import com.google.common.testing.EqualsTester;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClickEventTest {
  @Test
  void testEquality() throws MalformedURLException {
    new EqualsTester()
      .addEqualityGroup(
        ClickEvent.openUrl("https://google.com/"),
        ClickEvent.openUrl(new URL("https://google.com/")),
        ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://google.com/")
      )
      .addEqualityGroup(
        ClickEvent.runCommand("/test"),
        ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/test")
      )
      .addEqualityGroup(
        ClickEvent.suggestCommand("/test"),
        ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/test")
      )
      .addEqualityGroup(
        ClickEvent.changePage(1),
        ClickEvent.changePage("1"),
        ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, "1")
      )
      .addEqualityGroup(
        ClickEvent.copyToClipboard("test"),
        ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "test")
      )
      .testEquals();
  }

  @Test
  void assertReadable() {
    final Set<ClickEvent.Action> unreadable = Collections.singleton(ClickEvent.Action.OPEN_FILE);
    for(final ClickEvent.Action action : ClickEvent.Action.values()) {
      assertEquals(action.readable(), !unreadable.contains(action));
    }
  }

  @Test
  void testValue() {
    final ClickEvent event = ClickEvent.runCommand("hello world!");
    assertEquals("hello world!", event.value());
  }
}
