package com.example.urlshortener.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

  private static final Pattern BASE62_PATTERN = Pattern.compile("^[0-9A-Za-z]+$");

  @Test
  void encode_long_zero_returnsZeroString() {
    String encoded = Base62Encoder.encode(0L);
    assertNotNull(encoded);
    assertEquals("0", encoded);
    assertTrue(BASE62_PATTERN.matcher(encoded).matches());
  }

  @Test
  void encode_long_knownValues_areBase62() {
    String e1 = Base62Encoder.encode(1L);
    assertEquals("1", e1);
    assertTrue(BASE62_PATTERN.matcher(e1).matches());

    String e61 = Base62Encoder.encode(61L);
    assertEquals("z", e61);
    assertTrue(BASE62_PATTERN.matcher(e61).matches());

    String e62 = Base62Encoder.encode(62L);
    assertEquals("10", e62);
    assertTrue(BASE62_PATTERN.matcher(e62).matches());

    String eBig = Base62Encoder.encode(123_456_789L);
    assertNotNull(eBig);
    assertFalse(eBig.isEmpty());
    assertTrue(BASE62_PATTERN.matcher(eBig).matches());
  }

  @Test
  void encode_long_negative_throws() {
    assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1L));
  }

  @Test
  void encode_bytes_nullOrEmpty_returnsZeroString() {
    assertEquals("0", Base62Encoder.encode((byte[]) null));
    assertEquals("0", Base62Encoder.encode(new byte[0]));
  }

  @Test
  void encode_bytes_simpleArray_isBase62() {
    byte[] data = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
    String encoded = Base62Encoder.encode(data);
    assertNotNull(encoded);
    assertFalse(encoded.isEmpty());
    assertTrue(BASE62_PATTERN.matcher(encoded).matches());
  }
}


