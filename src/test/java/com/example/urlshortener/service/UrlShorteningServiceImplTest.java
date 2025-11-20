package com.example.urlshortener.service;

import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShorteningServiceImplTest {

  @Mock
  private ShortUrlRepository shortUrlRepository;

  @Mock
  private ShortCodeGenerator shortCodeGenerator;

  private UrlShorteningServiceImpl service;

  @Captor
  private ArgumentCaptor<String> seedCaptor;

  @BeforeEach
  void setUp() {
    service = new UrlShorteningServiceImpl(shortUrlRepository, shortCodeGenerator);
  }

  @Test
  void shortenUrl_newUrl_createsAndSaves() {
    String normalizedUrl = "https://example.com/new";
    String generatedCode = "abc123";

    when(shortUrlRepository.findByOriginalUrl(normalizedUrl)).thenReturn(Optional.empty());
    when(shortCodeGenerator.generateShortCode(normalizedUrl)).thenReturn(generatedCode);
    when(shortUrlRepository.findByShortCode(generatedCode)).thenReturn(Optional.empty());
    when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> {
      ShortUrl toSave = invocation.getArgument(0);
      toSave.setId(1L);
      return toSave;
    });

    ShortUrl result = service.shortenUrl(normalizedUrl);

    assertNotNull(result);
    assertEquals(normalizedUrl, result.getOriginalUrl());
    assertEquals(generatedCode, result.getShortCode());
    verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
  }

  @Test
  void shortenUrl_existingUrl_returnsExistingWithoutGeneratingOrSaving() {
    String normalizedUrl = "https://example.com/new";
    ShortUrl existing = new ShortUrl();
    existing.setId(42L);
    existing.setOriginalUrl(normalizedUrl);
    existing.setShortCode("existing");
    when(shortUrlRepository.findByOriginalUrl(normalizedUrl)).thenReturn(Optional.of(existing));

    ShortUrl result = service.shortenUrl(normalizedUrl);

    assertSame(existing, result);
    verify(shortCodeGenerator, never()).generateShortCode(anyString());
    verify(shortUrlRepository, never()).save(any(ShortUrl.class));
  }

  @Test
  void shortenUrl_collisionOnFirstAttempt_retriesAndSavesWithUniqueCode() {
    String normalizedUrl = "https://example.com/first";
    String firstCode = "dupCode";
    String secondCode = "uniqueCode";

    when(shortUrlRepository.findByOriginalUrl(normalizedUrl)).thenReturn(Optional.empty());
    // First attempt: generated code collides with a different URL
    when(shortCodeGenerator.generateShortCode(anyString())).thenReturn(firstCode, secondCode);

    ShortUrl collisionExisting = new ShortUrl();
    collisionExisting.setId(10L);
    collisionExisting.setOriginalUrl("https://example.com/other");
    collisionExisting.setShortCode(firstCode);
    when(shortUrlRepository.findByShortCode(firstCode)).thenReturn(Optional.of(collisionExisting));

    // Second attempt: code is free
    when(shortUrlRepository.findByShortCode(secondCode)).thenReturn(Optional.empty());
    when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> {
      ShortUrl toSave = invocation.getArgument(0);
      toSave.setId(11L);
      return toSave;
    });

    ShortUrl result = service.shortenUrl(normalizedUrl);

    assertNotNull(result);
    assertEquals(normalizedUrl, result.getOriginalUrl());
    assertEquals(secondCode, result.getShortCode());

    // Verify both collision check calls occurred
    verify(shortUrlRepository, times(1)).findByShortCode(firstCode);
    verify(shortUrlRepository, times(1)).findByShortCode(secondCode);
    verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));

    // Verify seeds used for generator attempts (first is URL as-is, then URL#1)
    verify(shortCodeGenerator, times(2)).generateShortCode(seedCaptor.capture());
    assertEquals(normalizedUrl, seedCaptor.getAllValues().get(0));
    assertEquals(normalizedUrl + "#1", seedCaptor.getAllValues().get(1));
  }

  @Test
  void resolveUrl_existingCode_returnsOriginalUrl() {
    String code = "abc123";
    String original = "https://example.com/foo";
    ShortUrl mapping = new ShortUrl();
    mapping.setId(5L);
    mapping.setOriginalUrl(original);
    mapping.setShortCode(code);
    when(shortUrlRepository.findByShortCode(code)).thenReturn(Optional.of(mapping));

    String resolved = service.resolveUrl(code);
    assertEquals(original, resolved);
  }

  @Test
  void resolveUrl_unknownCode_throwsUrlNotFound() {
    when(shortUrlRepository.findByShortCode("unknown")).thenReturn(Optional.empty());
    assertThrows(UrlNotFoundException.class, () -> service.resolveUrl("unknown"));
  }

  @Test
  void validation_shortenUrl_blankOrNull_throwsInvalidUrlException() {
    assertThrows(InvalidUrlException.class, () -> service.shortenUrl(null));
    assertThrows(InvalidUrlException.class, () -> service.shortenUrl("   "));
  }

  @Test
  void validation_resolveUrl_blankOrNull_throwsUrlNotFoundException() {
    assertThrows(UrlNotFoundException.class, () -> service.resolveUrl(null));
    assertThrows(UrlNotFoundException.class, () -> service.resolveUrl(""));
    assertThrows(UrlNotFoundException.class, () -> service.resolveUrl("   "));
  }
}


