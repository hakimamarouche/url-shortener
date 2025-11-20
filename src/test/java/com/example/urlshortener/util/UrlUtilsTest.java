package com.example.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilsTest {

  @Test
  void normalizeUrl_validInputs_returnsNormalizedString() {
    String v1 = UrlUtils.normalizeUrl("https://example.com");
    assertNotNull(v1);
    assertFalse(v1.isEmpty());
    assertEquals("https://example.com", v1);

    String v2 = UrlUtils.normalizeUrl("https://EXAMPLE.com/Path?x=1");
    assertNotNull(v2);
    assertFalse(v2.isEmpty());
    // scheme and host are lower-cased; path/query are preserved
    assertTrue(v2.startsWith("https://example.com/"));

    String v3 = UrlUtils.normalizeUrl("  https://example.com  ");
    assertNotNull(v3);
    assertFalse(v3.isEmpty());
    assertEquals("https://example.com", v3);
  }

  @Test
  void normalizeUrl_invalidInputs_throwIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizeUrl(null));
    assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizeUrl(""));
    assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizeUrl("   "));
    assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizeUrl("ftp://example.com"));
    assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizeUrl("not-a-url"));
  }
}


