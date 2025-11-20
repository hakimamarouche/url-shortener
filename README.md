## URL Shortener (Spring Boot)

A minimal URL shortener built with Spring Boot, JPA, and H2. It exposes a REST API and ships with Swagger UI via springdoc-openapi.

### Prerequisites
- **JDK 21** (required)
- No need to install Gradle; the project includes the Gradle Wrapper.

### Run (development)
- Windows PowerShell:

```bash
.\gradlew.bat bootRun
```

- macOS/Linux:

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

### Build a runnable JAR

```bash
./gradlew clean build
```

- Windows:

```bash
java -jar build\libs\url-shortener-0.0.1-SNAPSHOT.jar
```

- macOS/Linux:

```bash
java -jar build/libs/url-shortener-0.0.1-SNAPSHOT.jar
```

### Swagger / OpenAPI
- Swagger UI:
  - `http://localhost:8080/swagger-ui.html`
  - (or) `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

#### Using the API via Swagger UI
1. Start the app and open Swagger UI.
2. Expand `POST /api/shorten`, click “Try it out”, and use a body like:

```json
{
  "url": "https://example.com"
}
```

3. Execute the request. The response returns:
   - `shortUrl` (e.g., `http://localhost:8080/u/AbCd12`)
   - `code` (e.g., `AbCd12`)
   - `originalUrl`
4. To resolve:
   - Use `GET /api/resolve/{code}` in Swagger, or
   - Visit the redirect link directly in your browser: `http://localhost:8080/u/{code}`

### Configuration
- Default base URL used in responses is configured at:
  - `src/main/resources/application.properties` → `app.base-url=http://localhost:8080`
- To change the port, set `server.port` in the same file (e.g., `server.port=9090`) and update `app.base-url` accordingly.

### Optional (H2 Console)
- H2 console is enabled for development:
  - `http://localhost:8080/h2-console`
  - JDBC URL: see `spring.datasource.url` in `application.properties`
  - Username: `sa` (empty password by default)


