package com.example.urlshortener.exception;

/**
 * Thrown when a short code does not correspond to any known shortened URL.
 */
public class UrlNotFoundException extends RuntimeException {

  public UrlNotFoundException(String message) {
    super(message);
  }

  public UrlNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}


