package com.example.urlshortener.exception;

/**
 * Thrown when a provided URL is syntactically invalid or uses an unsupported scheme.
 */
public class InvalidUrlException extends RuntimeException {

  public InvalidUrlException(String message) {
    super(message);
  }

  public InvalidUrlException(String message, Throwable cause) {
    super(message, cause);
  }
}


