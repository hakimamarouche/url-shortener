package com.example.urlshortener.service;

import com.example.urlshortener.util.Base62Encoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generates deterministic short codes from a normalized URL string.
 *
 * <p>Input must be a <b>normalized</b> URL (e.g., produced by {@code UrlUtils.normalizeUrl}).
 * For the same normalized URL, this generator will always produce the same code.
 * Collisions are possible and intentionally not handled here; they should be managed
 * by higher layers.</p>
 */
@Component
public class ShortCodeGenerator {

  private static final int MAX_LENGTH = 10; // must be ≤ 10

  /**
   * Generate a deterministic short code for a normalized URL.
   *
   * <p>Behavior:</p>
   * <ul>
   *   <li>Validates that {@code normalizedUrl} is non-null and non-blank</li>
   *   <li>Computes SHA-256 hash of the input</li>
   *   <li>Uses the first 8 bytes to form a non-negative {@code long}</li>
   *   <li>Encodes the value using Base62</li>
   *   <li>Truncates the result to at most {@link #MAX_LENGTH} characters</li>
   * </ul>
   *
   * @param normalizedUrl a normalized URL string (must not be null/blank)
   * @return truncated Base62 short code
   * @throws IllegalArgumentException if {@code normalizedUrl} is null or blank
   */
  public String generateShortCode(String normalizedUrl) {
    if (normalizedUrl == null || normalizedUrl.isBlank()) {
      throw new IllegalArgumentException("normalizedUrl must not be null or blank");
    }

    byte[] hash = sha256(normalizedUrl);
    long value = firstEightBytesAsPositiveLong(hash);
    String base62 = Base62Encoder.encode(value);
    return base62.length() > MAX_LENGTH ? base62.substring(0, MAX_LENGTH) : base62;
  }

  private static byte[] sha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 MessageDigest not available", e);
    }
  }

  private static long firstEightBytesAsPositiveLong(byte[] bytes) {
    long value = 0L;
    for (int i = 0; i < 8; i++) {
      value = (value << 8) | (bytes[i] & 0xFFL);
    }
    // Ensure non-negative for Base62 long encoder
    return value & Long.MAX_VALUE;
  }
}


