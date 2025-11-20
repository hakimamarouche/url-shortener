package com.example.urlshortener.service;

import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ShortUrlRepository;
import com.example.urlshortener.util.UrlUtils;
import org.springframework.stereotype.Service;

@Service
public class UrlShorteningServiceImpl implements UrlShorteningService {

  private final ShortUrlRepository shortUrlRepository;
  private final ShortCodeGenerator shortCodeGenerator;

  public UrlShorteningServiceImpl(ShortUrlRepository shortUrlRepository,
      ShortCodeGenerator shortCodeGenerator) {
    this.shortUrlRepository = shortUrlRepository;
    this.shortCodeGenerator = shortCodeGenerator;
  }

  @Override
  public ShortUrl shortenUrl(String originalUrl) {
    if (originalUrl == null || originalUrl.isBlank()) {
      throw new InvalidUrlException("Original URL must not be null or blank");
    }

    final String normalizedUrl;
    try {
      normalizedUrl = UrlUtils.normalizeUrl(originalUrl);
    } catch (IllegalArgumentException ex) {
      throw new InvalidUrlException("Invalid URL: " + ex.getMessage(), ex);
    }

    // Idempotency: if mapping for this URL already exists, return it
    return shortUrlRepository.findByOriginalUrl(normalizedUrl)
        .orElseGet(() -> {
          final int maxAttempts = 10;
          for (int attemptIndex = 0; attemptIndex < maxAttempts; attemptIndex++) {
            String seed = candidateSeedForAttempt(normalizedUrl, attemptIndex);
            String candidateCode = shortCodeGenerator.generateShortCode(seed);

            // Check if this code is already taken
            var existingByCode = shortUrlRepository.findByShortCode(candidateCode);
            if (existingByCode.isEmpty()) {
              // Found a free code: create and persist the new mapping
              ShortUrl newMapping = new ShortUrl();
              newMapping.setOriginalUrl(normalizedUrl);
              newMapping.setShortCode(candidateCode);
              return shortUrlRepository.save(newMapping);
            }

            // If the code maps to the same original URL, return existing (idempotency safeguard)
            ShortUrl existing = existingByCode.get();
            if (normalizedUrl.equals(existing.getOriginalUrl())) {
              return existing;
            }

            // Otherwise, it's a collision with a different URL; continue deterministically
          }

          throw new IllegalStateException(
              "Unable to generate a unique short code after deterministic retries");
        });
  }

  @Override
  public String resolveUrl(String shortCode) {
    if (shortCode == null || shortCode.isBlank()) {
      throw new UrlNotFoundException("Short code must not be null or blank");
    }
    return shortUrlRepository.findByShortCode(shortCode)
        .map(ShortUrl::getOriginalUrl)
        .orElseThrow(() -> new UrlNotFoundException("Unknown short code: " + shortCode));
  }

  /**
   * Produces the deterministic seed used to generate a short code for a given attempt.
   * attemptIndex == 0 uses the normalized URL as-is; subsequent attempts append a stable suffix.
   */
  private String candidateSeedForAttempt(String normalizedUrl, int attemptIndex) {
    if (attemptIndex == 0) {
      return normalizedUrl;
    }
    return normalizedUrl + "#" + attemptIndex;
  }
}


