package com.example.urlshortener.web;

import com.example.urlshortener.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AppProperties appProperties;

	@Test
	@DisplayName("POST /api/shorten - happy path returns shortUrl, shortCode, originalUrl")
	void shorten_happyPath() throws Exception {
		String originalUrl = "https://example.com/path";
		String requestBody = objectMapper.createObjectNode()
			.put("url", originalUrl)
			.toString();

		String response = mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.shortUrl").exists())
			.andExpect(jsonPath("$.shortCode").exists())
			.andExpect(jsonPath("$.originalUrl").value(originalUrl))
			.andReturn()
			.getResponse()
			.getContentAsString();

		JsonNode json = objectMapper.readTree(response);
		String shortUrl = json.get("shortUrl").asText();

		String baseUrl = appProperties.getBaseUrl().replaceAll("/+$", "");
		assertThat(shortUrl).startsWith(baseUrl + "/u/");
	}

	@Test
	@DisplayName("POST /api/shorten - idempotent: same URL yields same shortCode")
	void shorten_idempotent_sameShortCode() throws Exception {
		String originalUrl = "https://example.com/idempotent";
		String requestBody = objectMapper.createObjectNode()
			.put("url", originalUrl)
			.toString();

		String resp1 = mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		String resp2 = mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		String code1 = objectMapper.readTree(resp1).get("shortCode").asText();
		String code2 = objectMapper.readTree(resp2).get("shortCode").asText();
		assertThat(code1).isEqualTo(code2);
	}

	@Test
	@DisplayName("GET /api/resolve/{code} - resolves a known code")
	void resolve_knownCode() throws Exception {
		// First create a mapping
		String originalUrl = "https://example.com/resolve-me";
		String createBody = objectMapper.createObjectNode()
			.put("url", originalUrl)
			.toString();
		String createResp = mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(createBody)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		String shortCode = objectMapper.readTree(createResp).get("shortCode").asText();

		// Resolve
		mockMvc.perform(get("/api/resolve/{code}", shortCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").value(originalUrl));
	}

	@Test
	@DisplayName("GET /u/{code} - redirects to original URL with 301 and Location header")
	void redirect_knownCode() throws Exception {
		// Create mapping
		String originalUrl = "https://example.com/redirect-me";
		String createBody = objectMapper.createObjectNode()
			.put("url", originalUrl)
			.toString();
		String createResp = mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(createBody)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		String shortCode = objectMapper.readTree(createResp).get("shortCode").asText();

		// Redirect
		mockMvc.perform(get("/u/{code}", shortCode))
			.andExpect(status().isMovedPermanently())
			.andExpect(header().string("Location", originalUrl));
	}

	@Test
	@DisplayName("POST /api/shorten - invalid URL yields 400 with error INVALID_URL")
	void shorten_invalidUrl_returnsBadRequest() throws Exception {
		String requestBody = objectMapper.createObjectNode()
			.put("url", "not-a-valid-url")
			.toString();

		mockMvc.perform(
				post("/api/shorten")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("INVALID_URL"))
			.andExpect(jsonPath("$.message").isNotEmpty());
	}

	@Test
	@DisplayName("GET /api/resolve/{code} - unknown code yields 404 with error NOT_FOUND")
	void resolve_unknownCode_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/resolve/{code}", "some-unknown-code"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error").value("NOT_FOUND"));
	}

	@Test
	@DisplayName("GET /u/{code} - unknown code yields 404")
	void redirect_unknownCode_returnsNotFound() throws Exception {
		mockMvc.perform(get("/u/{code}", "unknown-code"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error").value("NOT_FOUND"));
	}
}


