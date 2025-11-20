package com.example.urlshortener.web;

import com.example.urlshortener.config.AppProperties;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.service.UrlShorteningService;
import com.example.urlshortener.web.dto.ShortenRequest;
import com.example.urlshortener.web.dto.ShortenResponse;
import com.example.urlshortener.web.dto.ResolveResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UrlShorteningController {

  private final UrlShorteningService urlShorteningService;
  private final AppProperties appProperties;

  public UrlShorteningController(UrlShorteningService urlShorteningService, AppProperties appProperties) {
    this.urlShorteningService = urlShorteningService;
    this.appProperties = appProperties;
  }

  @PostMapping("/shorten")
  public ResponseEntity<ShortenResponse> shorten(
      @RequestBody @Valid ShortenRequest request
  ) {
    ShortUrl mapping = urlShorteningService.shortenUrl(request.url());
    String shortCode = mapping.getShortCode();

    String baseUrl = appProperties.getBaseUrl().replaceAll("/+$", "");
    String shortUrl = baseUrl + "/u/" + shortCode;

    ShortenResponse response = new ShortenResponse(shortUrl, shortCode, mapping.getOriginalUrl());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/resolve/{code}")
  public ResponseEntity<ResolveResponse> resolve(@PathVariable String code) {
    String originalUrl = urlShorteningService.resolveUrl(code);
    ResolveResponse response = new ResolveResponse(originalUrl);
    return ResponseEntity.ok(response);
  }
}


