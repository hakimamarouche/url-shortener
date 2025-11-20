package com.example.urlshortener.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Utility functions for validating and normalizing URLs used by the application.
 * <p>
 * Supported schemes are {@code http} and {@code https} (case-insensitive).
 * This class provides a canonicalization method that trims input, validates
 * syntax and allowed schemes, ensures the presence of a host, and returns a
 * normalized string form with lower-cased scheme and host.
 * </p>
 *
 * <p><b>Exceptions:</b></p>
 * <ul>
 *   <li>Throws {@link IllegalArgumentException} when the provided URL is null, empty,
 *       syntactically invalid, missing scheme/host, or uses a disallowed scheme.</li>
 * </ul>
 */
public final class UrlUtils {

  private UrlUtils() {
  }

  /**
   * Normalize and validate an input URL string for consistent internal use.
   *
   * <p>Behavior:</p>
   * <ul>
   *   <li>Trims the input</li>
   *   <li>Parses via {@link URI}</li>
   *   <li>Requires non-null scheme and host</li>
   *   <li>Only allows {@code http} or {@code https} schemes (case-insensitive)</li>
   *   <li>Lower-cases the scheme and host</li>
   *   <li>Returns the canonical {@link URI#toString()} representation</li>
   * </ul>
   *
   * @param url the input URL string
   * @return a normalized and validated URL string
   * @throws IllegalArgumentException if the URL is null/blank, syntactically invalid,
   *                                  missing scheme/host, or uses a disallowed scheme
   */
  public static String normalizeUrl(String url) {
    if (url == null) {
      throw new IllegalArgumentException("URL must not be null");
    }
    String trimmed = url.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("URL must not be empty");
    }

    final URI parsedUri;
    try {
      parsedUri = new URI(trimmed);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid URL syntax", e);
    }

    String scheme = parsedUri.getScheme();
    String host = parsedUri.getHost();
    if (scheme == null || host == null) {
      throw new IllegalArgumentException("URL must include scheme and host");
    }

    String lowerScheme = scheme.toLowerCase(Locale.ROOT);
    if (!"http".equals(lowerScheme) && !"https".equals(lowerScheme)) {
      throw new IllegalArgumentException("Only http and https schemes are allowed");
    }
    String lowerHost = host.toLowerCase(Locale.ROOT);

    String userInfo = parsedUri.getUserInfo();
    int port = parsedUri.getPort();
    String path = parsedUri.getPath();
    String query = parsedUri.getQuery();
    String fragment = parsedUri.getFragment();

    try {
      URI normalized = new URI(lowerScheme, userInfo, lowerHost, port, path, query, fragment).normalize();
      return normalized.toString();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid URL after normalization", e);
    }
  }

  /**
   * Validates whether a given URL string is a valid HTTP(S) URL that would be accepted
   * by {@link #normalizeUrl(String)}.
   *
   * @param url the input URL string
   * @return {@code true} if valid and allowed; {@code false} otherwise
   */
  public static boolean isValidHttpUrl(String url) {
    try {
      normalizeUrl(url);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }
}


