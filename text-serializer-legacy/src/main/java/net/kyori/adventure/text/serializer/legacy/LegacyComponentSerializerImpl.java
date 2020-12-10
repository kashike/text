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
package net.kyori.adventure.text.serializer.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

final class LegacyComponentSerializerImpl implements LegacyComponentSerializer {
  static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();
  private static final char LEGACY_BUNGEE_HEX_CHAR = 'x';
  private static final List<TextFormat> FORMATS;
  private static final String LEGACY_CHARS;

  static {
    // Enumeration order may change - manually

    final Map<TextFormat, String> formats = new LinkedHashMap<>(16 + 5 + 1); // colours + decorations + reset

    formats.put(NamedTextColor.BLACK, "0");
    formats.put(NamedTextColor.DARK_BLUE, "1");
    formats.put(NamedTextColor.DARK_GREEN, "2");
    formats.put(NamedTextColor.DARK_AQUA, "3");
    formats.put(NamedTextColor.DARK_RED, "4");
    formats.put(NamedTextColor.DARK_PURPLE, "5");
    formats.put(NamedTextColor.GOLD, "6");
    formats.put(NamedTextColor.GRAY, "7");
    formats.put(NamedTextColor.DARK_GRAY, "8");
    formats.put(NamedTextColor.BLUE, "9");
    formats.put(NamedTextColor.GREEN, "a");
    formats.put(NamedTextColor.AQUA, "b");
    formats.put(NamedTextColor.RED, "c");
    formats.put(NamedTextColor.LIGHT_PURPLE, "d");
    formats.put(NamedTextColor.YELLOW, "e");
    formats.put(NamedTextColor.WHITE, "f");

    formats.put(TextDecoration.OBFUSCATED, "k");
    formats.put(TextDecoration.BOLD, "l");
    formats.put(TextDecoration.STRIKETHROUGH, "m");
    formats.put(TextDecoration.UNDERLINED, "n");
    formats.put(TextDecoration.ITALIC, "o");

    formats.put(Reset.INSTANCE, "r");

    FORMATS = Collections.unmodifiableList(new ArrayList<>(formats.keySet()));
    LEGACY_CHARS = String.join("", formats.values());

    // assert same length
    if(FORMATS.size() != LEGACY_CHARS.length()) {
      throw new IllegalStateException("FORMATS length differs from LEGACY_CHARS length");
    }
  }

  static final LegacyComponentSerializer SECTION_SERIALIZER = new LegacyComponentSerializerImpl(SECTION_CHAR, HEX_CHAR, null, false, false);
  static final LegacyComponentSerializer AMPERSAND_SERIALIZER = new LegacyComponentSerializerImpl(AMPERSAND_CHAR, HEX_CHAR, null, false, false);

  private final char character;
  private final char hexCharacter;
  private final @Nullable TextReplacementConfig urlReplacementConfig;
  private final boolean hexColours;
  private final boolean useTerriblyStupidHexFormat; // (╯°□°)╯︵ ┻━┻

  LegacyComponentSerializerImpl(final char character, final char hexCharacter, final @Nullable TextReplacementConfig urlReplacementConfig, final boolean hexColours, final boolean useTerriblyStupidHexFormat) {
    this.character = character;
    this.hexCharacter = hexCharacter;
    this.urlReplacementConfig = urlReplacementConfig;
    this.hexColours = hexColours;
    this.useTerriblyStupidHexFormat = useTerriblyStupidHexFormat;
  }

  private @Nullable FormatCodeType determineFormatType(final char legacy, final String input, final int pos) {
    if(pos >= 14) {
      // The BungeeCord RGB color format uses a repeating sequence of RGB values, each character formatted
      // as their own color format string, and to make things interesting, all the colors are also valid
      // Mojang colors. To differentiate this, we do a lookback check for &x (or equivalent) for its position
      // in the string if it is indeed a BungeeCord-style RGB color.
      final int expectedCharacterPosition = pos - 14;
      final int expectedIndicatorPosition = pos - 13;
      if(input.charAt(expectedCharacterPosition) == this.character && input.charAt(expectedIndicatorPosition) == LEGACY_BUNGEE_HEX_CHAR) {
        return FormatCodeType.BUNGEECORD_UNUSUAL_HEX;
      }
    }
    if(legacy == this.hexCharacter) {
      return FormatCodeType.KYORI_HEX;
    } else if(LEGACY_CHARS.indexOf(legacy) != -1) {
      return FormatCodeType.MOJANG_LEGACY;
    }
    return null;
  }

  static @Nullable LegacyFormat legacyFormat(final char character) {
    final int index = LEGACY_CHARS.indexOf(character);
    if(index != -1) {
      final TextFormat format = FORMATS.get(index);
      if(format instanceof NamedTextColor) {
        return new LegacyFormat((NamedTextColor) format);
      } else if(format instanceof TextDecoration) {
        return new LegacyFormat((TextDecoration) format);
      } else if(format instanceof Reset) {
        return LegacyFormat.RESET;
      }
    }
    return null;
  }

  private @Nullable DecodedFormat decodeTextFormat(final char legacy, final String input, final int pos) {
    final FormatCodeType foundFormat = this.determineFormatType(legacy, input, pos);
    if(foundFormat == null) {
      return null;
    }
    if(foundFormat == FormatCodeType.KYORI_HEX) {
      return new DecodedFormat(foundFormat, TextColor.fromHexString('#' + input.substring(pos, pos + 6)));
    } else if(foundFormat == FormatCodeType.MOJANG_LEGACY) {
      return new DecodedFormat(foundFormat, FORMATS.get(LEGACY_CHARS.indexOf(legacy)));
    } else if(foundFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
      final StringBuilder foundHex = new StringBuilder();
      for(int i = pos - 1; i >= pos - 11; i -= 2) {
        foundHex.append(input.charAt(i));
      }
      foundHex.append('#');
      return new DecodedFormat(foundFormat, TextColor.fromHexString(foundHex.reverse().toString()));
    }
    return null;
  }

  private static boolean isHexTextColor(final TextFormat format) {
    return format instanceof TextColor && !(format instanceof NamedTextColor);
  }

  private String toLegacyCode(TextFormat format) {
    if(isHexTextColor(format)) {
      final TextColor color = (TextColor) format;
      if(this.hexColours) {
        final String hex = String.format("%06x", color.value());
        if(this.useTerriblyStupidHexFormat) {
          // ah yes, wonderful. A 14 digit long completely unreadable string.
          final StringBuilder legacy = new StringBuilder(String.valueOf(LEGACY_BUNGEE_HEX_CHAR));
          for(final char character : hex.toCharArray()) {
            legacy.append(this.character).append(character);
          }
          return legacy.toString();
        } else {
          // this is a bit nicer, hey?
          return this.hexCharacter + hex;
        }
      } else {
        // if we are not using hex colours, then convert the hex colour
        // to the "nearest" possible named/standard text colour
        format = NamedTextColor.nearestTo(color);
      }
    }
    final int index = FORMATS.indexOf(format);
    return Character.toString(LEGACY_CHARS.charAt(index));
  }

  private TextComponent extractUrl(final TextComponent component) {
    if(this.urlReplacementConfig == null) return component;
    final Component newComponent = component.replaceText(this.urlReplacementConfig);
    if(newComponent instanceof TextComponent) return (TextComponent) newComponent;
    return TextComponent.ofChildren(newComponent);
  }

  @Override
  public @NonNull TextComponent deserialize(final @NonNull String input) {
    int next = input.lastIndexOf(this.character, input.length() - 2);
    if(next == -1) {
      return this.extractUrl(Component.text(input));
    }

    final List<TextComponent> parts = new ArrayList<>();

    TextComponent.Builder current = null;
    boolean reset = false;

    int pos = input.length();
    do {
      final DecodedFormat decoded = this.decodeTextFormat(input.charAt(next + 1), input, next + 2);
      if(decoded != null) {
        final int from = next + (decoded.encodedFormat == FormatCodeType.KYORI_HEX ? 8 : 2);
        if(from != pos) {
          if(current != null) {
            if(reset) {
              parts.add(current.build());
              reset = false;
              current = Component.textBuilder();
            } else {
              current = Component.textBuilder().append(current.build());
            }
          } else {
            current = Component.textBuilder();
          }

          current.content(input.substring(from, pos));
        } else if(current == null) {
          current = Component.textBuilder();
        }

        if(!reset) {
          reset = applyFormat(current, decoded.format);
        }
        if(decoded.encodedFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
          // BungeeCord hex characters are a repeating set of characters, all of which are also valid
          // legacy Mojang chat colors. Subtract the number of characters in the format, and only then
          // skip ahead.
          next -= 12;
        }
        pos = next;
      }

      next = input.lastIndexOf(this.character, next - 1);
    } while(next != -1);

    if(current != null) {
      parts.add(current.build());
    }

    final String remaining = pos > 0 ? input.substring(0, pos) : "";
    if(parts.size() == 1 && remaining.isEmpty()) {
      return this.extractUrl(parts.get(0));
    } else {
      Collections.reverse(parts);
      return this.extractUrl(Component.textBuilder().content(remaining).append(parts).build());
    }
  }

  @Override
  public @NonNull String serialize(final @NonNull Component component) {
    final Cereal state = new Cereal();
    state.append(component);
    return state.toString();
  }

  private static boolean applyFormat(final TextComponent.@NonNull Builder builder, final @NonNull TextFormat format) {
    if(format instanceof TextColor) {
      builder.colorIfAbsent((TextColor) format);
      return true;
    } else if(format instanceof TextDecoration) {
      builder.decoration((TextDecoration) format, TextDecoration.State.TRUE);
      return false;
    } else if(format instanceof Reset) {
      return true;
    }
    throw new IllegalArgumentException(String.format("unknown format '%s'", format.getClass()));
  }

  @Override
  public @NonNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  private enum Reset implements TextFormat {
    INSTANCE
  }

  // Are you hungry?
  private final class Cereal {
    private final StringBuilder sb = new StringBuilder();
    private final StyleState style = new StyleState();
    private @Nullable TextFormat format;

    void append(final @NonNull Component component) {
      this.append(component, new StyleState());
    }

    private void append(final @NonNull Component component, final @NonNull StyleState style) {
      style.apply(component);

      if(component instanceof TextComponent) {
        final String content = ((TextComponent) component).content();
        if(!content.isEmpty()) {
          style.applyFormat();
          this.sb.append(content);
        }
      }

      final List<Component> children = component.children();
      if(!children.isEmpty()) {
        final StyleState childrenStyle = new StyleState(style);
        for(final Component child : children) {
          this.append(child, childrenStyle);
          childrenStyle.set(style);
        }
      }

      if(!style.noColorOrDecorations()) {
        this.append(Reset.INSTANCE);
      }
    }

    void append(final @NonNull TextFormat format) {
      if(this.format != format) {
        this.sb.append(LegacyComponentSerializerImpl.this.character).append(LegacyComponentSerializerImpl.this.toLegacyCode(format));
      }
      this.format = format;
    }

    @Override
    public String toString() {
      return this.sb.toString();
    }

    private final class StyleState {
      private @Nullable TextColor color;
      private final Set<TextDecoration> decorations;

      StyleState() {
        this.decorations = EnumSet.noneOf(TextDecoration.class);
      }

      StyleState(final @NonNull StyleState that) {
        this.color = that.color;
        this.decorations = EnumSet.copyOf(that.decorations);
      }

      boolean noColorOrDecorations() {
        return this.color == null || this.decorations.isEmpty();
      }

      void set(final @NonNull StyleState that) {
        this.color = that.color;
        this.decorations.clear();
        this.decorations.addAll(that.decorations);
      }

      void apply(final @NonNull Component component) {
        final TextColor color = component.color();
        if(color != null) {
          this.color = color;
        }

        for(int i = 0, length = DECORATIONS.length; i < length; i++) {
          final TextDecoration decoration = DECORATIONS[i];
          switch(component.decoration(decoration)) {
            case TRUE:
              this.decorations.add(decoration);
              break;
            case FALSE:
              this.decorations.remove(decoration);
              break;
          }
        }
      }

      void applyFormat() {
        // If color changes, we need to do a full reset.
        // Additionally, if the last thing to be appended was a reset then we need to re-apply everything.
        if(this.color != Cereal.this.style.color || Cereal.this.format == Reset.INSTANCE) {
          this.applyFullFormat();
          return;
        }

        // Does current have any decorations we don't have?
        // Since there is no way to undo decorations, we need to reset these cases
        if(!this.decorations.containsAll(Cereal.this.style.decorations)) {
          this.applyFullFormat();
          return;
        }

        // Apply new decorations
        for(final TextDecoration decoration : this.decorations) {
          if(Cereal.this.style.decorations.add(decoration)) {
            Cereal.this.append(decoration);
          }
        }
      }

      private void applyFullFormat() {
        if(this.color != null) {
          Cereal.this.append(this.color);
        } else {
          Cereal.this.append(Reset.INSTANCE);
        }
        Cereal.this.style.color = this.color;

        for(final TextDecoration decoration : this.decorations) {
          Cereal.this.append(decoration);
        }

        Cereal.this.style.decorations.clear();
        Cereal.this.style.decorations.addAll(this.decorations);
      }
    }
  }

  static final class BuilderImpl implements Builder {
    private char character = LegacyComponentSerializer.SECTION_CHAR;
    private char hexCharacter = LegacyComponentSerializer.HEX_CHAR;
    private TextReplacementConfig urlReplacementConfig = null;
    private boolean hexColours = false;
    private boolean useTerriblyStupidHexFormat = false;

    BuilderImpl() {
    }

    BuilderImpl(final @NonNull LegacyComponentSerializerImpl serializer) {
      this.character = serializer.character;
      this.hexCharacter = serializer.hexCharacter;
      this.urlReplacementConfig = serializer.urlReplacementConfig;
      this.hexColours = serializer.hexColours;
      this.useTerriblyStupidHexFormat = serializer.useTerriblyStupidHexFormat;
    }

    @Override
    public @NonNull Builder character(final char legacyCharacter) {
      this.character = legacyCharacter;
      return this;
    }

    @Override
    public @NonNull Builder hexCharacter(final char legacyHexCharacter) {
      this.hexCharacter = legacyHexCharacter;
      return this;
    }

    @Override
    public @NonNull Builder extractUrls() {
      return this.extractUrls(DEFAULT_URL_PATTERN, null);
    }

    @Override
    public @NonNull Builder extractUrls(final @NonNull Pattern pattern) {
      return this.extractUrls(pattern, null);
    }

    @Override
    public @NonNull Builder extractUrls(final @Nullable Style style) {
      return this.extractUrls(DEFAULT_URL_PATTERN, style);
    }

    @Override
    public @NonNull Builder extractUrls(final @NonNull Pattern pattern, final @Nullable Style style) {
      requireNonNull(pattern, "pattern");
      this.urlReplacementConfig = TextReplacementConfig.builder()
        .match(pattern)
        .replacement(url -> (style == null ? url : url.style(style)).clickEvent(ClickEvent.openUrl(url.content())))
        .build();
      return this;
    }

    @Override
    public @NonNull Builder hexColors() {
      this.hexColours = true;
      return this;
    }

    @Override
    public @NonNull Builder useUnusualXRepeatedCharacterHexFormat() {
      this.useTerriblyStupidHexFormat = true; // :(
      return this;
    }

    @Override
    public @NonNull LegacyComponentSerializer build() {
      return new LegacyComponentSerializerImpl(this.character, this.hexCharacter, this.urlReplacementConfig, this.hexColours, this.useTerriblyStupidHexFormat);
    }
  }

  enum FormatCodeType {
    MOJANG_LEGACY,
    KYORI_HEX,
    BUNGEECORD_UNUSUAL_HEX;
  }

  static final class DecodedFormat {
    final FormatCodeType encodedFormat;
    final TextFormat format;

    private DecodedFormat(final FormatCodeType encodedFormat, final TextFormat format) {
      if(format == null) {
        throw new IllegalStateException("No format found");
      }
      this.encodedFormat = encodedFormat;
      this.format = format;
    }
  }
}
