package com.voghan.pillar.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voghan.pillar.client.config.OAuthConfig;
import com.voghan.pillar.client.config.impl.LocalAuthorConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SimpleOAuthClient implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleOAuthClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final CloseableHttpClient httpClient;

    public SimpleOAuthClient(String tokenUrl, String clientId, String clientSecret) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        httpClient = HttpClientBuilder.create().build();
    }

    public static void main(String[] args) {
        OAuthConfig oauthConfig = new LocalAuthorConfig();

        try(SimpleOAuthClient client = new SimpleOAuthClient(
                oauthConfig.getOauthTokenEndpoint(),
                oauthConfig.getClientId(),
                oauthConfig.getClientSecret())) {
            String jwt = oauthConfig.createJWT();
            if (jwt == null) throw new RuntimeException("Failed to generate jwt");
            LOGGER.info("jwt successfully acquired");

            String accessToken = client.requestAccessToken(jwt);
            if (accessToken == null) throw new RuntimeException("Failed to generate access token");
            LOGGER.info("access_token successfully acquired");

            //http://localhost:4502/graphql/execute.json/pillar/all-hero-cards
            String endpoint = oauthConfig.getOauthServer() + "/graphql/execute.json/pillar/all-hero-cards";
            String content = client.getContent(accessToken, endpoint);
            if (content != null) {
                LOGGER.info("Content: \n{}", content);
            }

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContent(String accessToken, String endpoint) throws IOException {
        HttpGet request = new HttpGet(endpoint);
        request.setHeader("Authorization", "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("Response Status Code: {}", statusCode);

            HttpEntity entity = response.getEntity();
            if (entity == null) return null;
            String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            if (statusCode == HttpStatus.SC_OK) {
                return content;
            } else {
                LOGGER.warn(response.getStatusLine().getReasonPhrase());
                LOGGER.warn("Response: {}", content);
            }
        }

        return null;
    }

    public String requestAccessToken(String jwtToken) throws IOException {
        HttpUriRequest request = RequestBuilder
            .post().setUri(tokenUrl)
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setHeader("Accept", "application/json")
            .addParameter("assertion", jwtToken)
            .addParameter("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
            .addParameter("client_id", clientId)
            .addParameter("client_secret", clientSecret)
            .build();

        LOGGER.debug(request.toString());
        String accessToken = null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode== HttpStatus.SC_OK &&
                    entity != null) {
                String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
                JsonNode jsonNode = MAPPER.readTree(content);
                accessToken = jsonNode.path("access_token").asText(null);
            } else {
                LOGGER.warn("Status code:{}", statusCode);
                LOGGER.warn(response.getStatusLine().getReasonPhrase());
            }
        }
        return accessToken;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
