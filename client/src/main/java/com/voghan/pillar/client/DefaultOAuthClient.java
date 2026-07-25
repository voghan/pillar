package com.voghan.pillar.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voghan.pillar.client.config.OAuthConfig;
import com.voghan.pillar.client.config.impl.LocalAuthorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultOAuthClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOAuthClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final HttpClient httpClient;


    public static void main(String[] args) {
        OAuthConfig oauthConfig = new LocalAuthorConfig();

        try {
            // Client Credentials (e.g. calling an API as a service)
            DefaultOAuthClient authClient = new DefaultOAuthClient(
                    oauthConfig.getOauthTokenEndpoint(),
                    oauthConfig.getClientId(),
                    oauthConfig.getClientSecret());

            String jwt = oauthConfig.createJWT();
            LOGGER.info("jwt:{}", jwt);
            if (jwt == null) throw new RuntimeException("Failed to generate jwt");

            TokenResponse token = authClient.requestAccessToken("authorization_code", jwt);
            LOGGER.info("Access token: {}", token.accessToken());

            token = authClient.refresh(token.refreshToken());
            LOGGER.info("Refresh token: {}", token.refreshToken());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultOAuthClient(String tokenUrl, String clientId, String clientSecret) {
        this.tokenUrl    = tokenUrl;
        this.clientId    = clientId;
        this.clientSecret = clientSecret;
        this.httpClient  = HttpClient.newHttpClient();
    }

    public TokenResponse requestAccessToken(String scope, String jwtToken) throws Exception {
        Map<String, String> params = Map.of(
                "grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer",
                "scope",      scope,
                "assertion", jwtToken
        );
        return post(params, true);
    }

    // --- Refresh Token grant ---
    public TokenResponse refresh(String refreshToken) throws Exception {
        Map<String, String> params = Map.of(
                "grant_type",    "refresh_token",
                "refresh_token", refreshToken
        );
        return post(params, true);
    }

    // --- HTTP ---
    private TokenResponse post(Map<String, String> params, boolean useBasicAuth) throws Exception {
        String body = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body));

        if (useBasicAuth) {
            String credentials = java.util.Base64.getEncoder().encodeToString(
                    (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
            builder.header("Authorization", "Basic " + credentials);
        }

        HttpResponse<String> response = httpClient.send(
                builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Token request failed: "
                    + response.statusCode() + " " + response.body());
        }

        return TokenResponse.parse(response.body());
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // --- Token response ---

    public record TokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long   expiresIn,
            String scope
    ) {
        /** Minimal JSON parse — use Jackson/Gson in production */
        public static TokenResponse parse(String json) throws JsonProcessingException {
            return new TokenResponse(
                    extract(json, "access_token"),
                    extract(json, "refresh_token"),
                    extract(json, "token_type"),
                    Long.parseLong(extractOrDefault(json, "expires_in", "0")),
                    extract(json, "scope")
            );
        }

        private static String extract(String json, String key) throws JsonProcessingException {
            return extractOrDefault(json, key, null);
        }

        private static String extractOrDefault(String json, String key, String defaultValue) throws JsonProcessingException {
            JsonNode jsonNode = MAPPER.readTree(json);
            return jsonNode.path(key).asText(defaultValue);
        }
    }
}
