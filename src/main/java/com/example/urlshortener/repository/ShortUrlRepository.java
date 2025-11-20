package com.example.urlshortener.repository;

import com.example.urlshortener.model.ShortUrl;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  Optional<ShortUrl> findByOriginalUrl(String originalUrl);
  Optional<ShortUrl> findByShortCode(String shortCode);
}


