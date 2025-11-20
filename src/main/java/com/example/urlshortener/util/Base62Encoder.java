package com.example.urlshortener.util;

import java.math.BigInteger;

/**
 * Base62 encoder utility producing URL-safe, compact strings using the alphabet
 * {@code 0-9A-Za-z}. This class is a static utility and not intended to be instantiated.
 */
public final class Base62Encoder {

  private static final char[] ALPHABET =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

  private static final BigInteger SIXTY_TWO = BigInteger.valueOf(62);

  private Base62Encoder() {
  }

  /**
   * Encode a non-negative {@code long} value to a Base62 string.
   *
   * <p>Assumptions and behavior:</p>
   * <ul>
   *   <li>Input must be {@code >= 0}, otherwise {@link IllegalArgumentException} is thrown.</li>
   *   <li>Value {@code 0} encodes to {@code "0"}.</li>
   *   <li>Uses alphabet {@code 0-9A-Za-z}.</li>
   * </ul>
   *
   * @param value non-negative value to encode
   * @return Base62 string representation
   * @throws IllegalArgumentException if {@code value} is negative
   */
  public static String encode(long value) {
    if (value < 0) {
      throw new IllegalArgumentException("value must be non-negative");
    }
    if (value == 0) {
      return "0";
    }
    // Maximum Base62 length for unsigned long is 11 (since 62^11 > 2^64)
    char[] buffer = new char[11];
    int index = buffer.length;
    long current = value;
    while (current > 0) {
      int remainder = (int) (current % 62);
      current = current / 62;
      buffer[--index] = ALPHABET[remainder];
    }
    return new String(buffer, index, buffer.length - index);
  }

  /**
   * Encode an unsigned big-endian integer represented as a byte array to Base62.
   *
   * <p>Behavior:</p>
   * <ul>
   *   <li>Treats {@code bytes} as an unsigned big-endian integer.</li>
   *   <li>Null or empty input encodes to {@code "0"}.</li>
   *   <li>Uses alphabet {@code 0-9A-Za-z}.</li>
   * </ul>
   *
   * @param bytes unsigned big-endian integer bytes
   * @return Base62 string representation
   */
  public static String encode(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return "0";
    }
    // Interpret as positive big integer (signum = 1)
    BigInteger number = new BigInteger(1, bytes);
    if (number.equals(BigInteger.ZERO)) {
      return "0";
    }
    StringBuilder result = new StringBuilder();
    BigInteger current = number;
    while (current.signum() > 0) {
      BigInteger[] dr = current.divideAndRemainder(SIXTY_TWO);
      int remainder = dr[1].intValue(); // 0..61
      result.append(ALPHABET[remainder]);
      current = dr[0];
    }
    return result.reverse().toString();
  }
}


