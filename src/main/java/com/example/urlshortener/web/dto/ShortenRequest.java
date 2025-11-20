package com.example.urlshortener.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(@NotBlank String url) {}


