package com.example.urlshortener.service;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

  private static final Pattern BASE62_PATTERN = Pattern.compile("^[0-9A-Za-z]+$");

  @Test
  void generateShortCode_sameInput_isDeterministic() {
    ShortCodeGenerator generator = new ShortCodeGenerator();
    String input = "https://example.com/a";
    String code1 = generator.generateShortCode(input);
    String code2 = generator.generateShortCode(input);
    assertNotNull(code1);
    assertEquals(code1, code2);
    assertTrue(BASE62_PATTERN.matcher(code1).matches());
    assertTrue(code1.length() > 0 && code1.length() <= 8);
  }

  @Test
  void generateShortCode_differentInputs_usuallyDifferent() {
    ShortCodeGenerator generator = new ShortCodeGenerator();
    String codeA = generator.generateShortCode("https://example.com/a");
    String codeB = generator.generateShortCode("https://example.com/b");
    assertNotNull(codeA);
    assertNotNull(codeB);
    assertTrue(BASE62_PATTERN.matcher(codeA).matches());
    assertTrue(BASE62_PATTERN.matcher(codeB).matches());
    assertTrue(codeA.length() > 0 && codeA.length() <= 8);
    assertTrue(codeB.length() > 0 && codeB.length() <= 8);
    assertNotEquals(codeA, codeB);
  }

  @Test
  void generateShortCode_invalidInput_throws() {
    ShortCodeGenerator generator = new ShortCodeGenerator();
    assertThrows(IllegalArgumentException.class, () -> generator.generateShortCode(null));
    assertThrows(IllegalArgumentException.class, () -> generator.generateShortCode(""));
    assertThrows(IllegalArgumentException.class, () -> generator.generateShortCode("   "));
  }
}


