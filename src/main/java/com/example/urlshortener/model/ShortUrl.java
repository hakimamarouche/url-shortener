package com.example.urlshortener.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
    name = "short_urls",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_short_urls_original_url",
          columnNames = "original_url"),
      @UniqueConstraint(
          name = "uk_short_urls_short_code",
          columnNames = "short_code")
    })
public class ShortUrl {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_url", nullable = false, length = 2048)
  private String originalUrl;

  @Column(name = "short_code", nullable = false, length = 10)
  private String shortCode;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public ShortUrl() {
  }

  public ShortUrl(Long id, String originalUrl, String shortCode, Instant createdAt) {
    this.id = id;
    this.originalUrl = originalUrl;
    this.shortCode = shortCode;
    this.createdAt = createdAt;
  }

  @PrePersist
  private void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = Instant.now();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public String getShortCode() {
    return shortCode;
  }

  public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "ShortUrl{" +
        "id=" + id +
        ", originalUrl='" + originalUrl + '\'' +
        ", shortCode='" + shortCode + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}


