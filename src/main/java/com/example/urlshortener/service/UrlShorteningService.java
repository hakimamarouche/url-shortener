package com.example.urlshortener.service;

import com.example.urlshortener.model.ShortUrl;

/**
 * Core service interface for URL shortening operations.
 * <p>
 * This interface is intentionally independent of any web or transport concerns.
 * Implementations will encapsulate business logic for creating and resolving short URLs.
 */
public interface UrlShorteningService {

	/**
	 * Shortens the provided raw original URL string.
	 * <p>
	 * The input is the raw, non-normalized URL; normalization, validation, persistence,
	 * and short code generation are responsibilities of the implementation.
	 * <p>
	 * Implementations may throw appropriate runtime exceptions on invalid input
	 * (e.g., malformed URL, unsupported scheme) or persistence errors.
	 *
	 * @param originalUrl the raw original URL to shorten (not yet normalized)
	 * @return the persisted {@link ShortUrl} entity representing the mapping
	 */
	ShortUrl shortenUrl(String originalUrl);

	/**
	 * Resolves the given short code to the original URL string.
	 * <p>
	 * Implementations may throw appropriate runtime exceptions when the input is invalid
	 * or when the short code does not correspond to a known mapping.
	 *
	 * @param shortCode the short code to resolve
	 * @return the original URL string if found
	 */
	String resolveUrl(String shortCode);
}


