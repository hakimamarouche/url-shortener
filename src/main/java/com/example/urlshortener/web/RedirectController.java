package com.example.urlshortener.web;

import com.example.urlshortener.service.UrlShorteningService;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

  private final UrlShorteningService urlShorteningService;

  public RedirectController(UrlShorteningService urlShorteningService) {
    this.urlShorteningService = urlShorteningService;
  }

  @GetMapping("/u/{code}")
  public ResponseEntity<Void> redirect(@PathVariable String code) {
    String originalUrl = urlShorteningService.resolveUrl(code);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(originalUrl));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }
}


