package com.voghan.pillar.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimpleOAuthClientTest {

    @Mock
    RequestBuilder requestBuilder;

    @Mock
    HttpUriRequest request;

    @Mock
    HttpClientBuilder clientBuilder;

    @Mock
    CloseableHttpClient closeableHttpClient;

    @Mock
    CloseableHttpResponse response;

    @Mock
    HttpEntity entity;

    @Mock
    InputStream inputStream;

    @Mock
    StatusLine statusLine;

    @BeforeEach
    void setup() throws IOException {
        String tokenUrl = "https://token-url";

        // Shared plumbing for the requestAccessToken tests. main_default() drives a different
        // path (mocked construction) and uses none of these, so keep them lenient to avoid
        // UnnecessaryStubbingException under STRICT_STUBS.
        lenient().when(requestBuilder.build()).thenReturn(request);
        lenient().when(requestBuilder.setUri(tokenUrl)).thenReturn(requestBuilder);
        lenient().when(requestBuilder.setHeader(any(),any())).thenReturn(requestBuilder);
        lenient().when(requestBuilder.addParameter(any(),any())).thenReturn(requestBuilder);
        lenient().when(clientBuilder.build()).thenReturn(closeableHttpClient);
        lenient().when(closeableHttpClient.execute(any())).thenReturn(response);
        lenient().when(response.getEntity()).thenReturn(entity);
        lenient().when(response.getStatusLine()).thenReturn(statusLine);
    }

    @Test
    void main_executesMocks() {
        String[] arguments = {};

        // main() constructs its own SimpleOAuthClient and would otherwise hit the network
        // (requestAccessToken -> POST tokenUrl, then getContent). Intercept that construction
        // and stub the token call so no real HTTP happens; getContent is a mock no-op.
        try (MockedConstruction<SimpleOAuthClient> mockedClient = mockConstruction(
                SimpleOAuthClient.class,
                (mock, context) -> when(mock.requestAccessToken(any())).thenReturn("access_token_value"))) {

            assertDoesNotThrow(() -> SimpleOAuthClient.main(arguments));

        }
    }

    @Test
    void requestAccessToken_returns200Status() throws Exception {

        String tokenUrl = "https://token-url";
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String jwtToken = "jwt_token";
        String responseJson = "{\"access_token\":\"access_token_value\",\"expires_in\":3600}";
        String expected = "access_token_value";

        try (MockedStatic<RequestBuilder> mockedRequestBuilder = mockStatic(RequestBuilder.class);
            MockedStatic<HttpClientBuilder> mockedHttpClientBuilder = mockStatic(HttpClientBuilder.class);
            MockedStatic<IOUtils> mockedIOUtils = mockStatic(IOUtils.class)) {
            mockedRequestBuilder.when(() -> RequestBuilder.post()).thenReturn(requestBuilder);
            mockedHttpClientBuilder.when(() -> HttpClientBuilder.create()).thenReturn(clientBuilder);
            mockedIOUtils.when(()->IOUtils.toString(eq(inputStream), eq(StandardCharsets.UTF_8))).thenReturn(responseJson);

            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            when(entity.getContent()).thenReturn(inputStream);

            SimpleOAuthClient client = new SimpleOAuthClient(tokenUrl, clientId, clientSecret);
            String accessToken = client.requestAccessToken(jwtToken);

            assertNotNull(accessToken);
            assertEquals(expected, accessToken);
        }
    }

    @Test
    void requestAccessToken_returns401Status() throws Exception {

        String tokenUrl = "https://token-url";
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String jwtToken = "jwt_token";

        try (MockedStatic<RequestBuilder> mockedRequestBuilder = mockStatic(RequestBuilder.class);
             MockedStatic<HttpClientBuilder> mockedHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            mockedRequestBuilder.when(() -> RequestBuilder.post()).thenReturn(requestBuilder);
            mockedHttpClientBuilder.when(() -> HttpClientBuilder.create()).thenReturn(clientBuilder);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);

            SimpleOAuthClient client = new SimpleOAuthClient(tokenUrl, clientId, clientSecret);
            String accessToken = client.requestAccessToken(jwtToken);
            assertNull(accessToken);
        }
    }

    @Test
    void getContent_returns4200Status() throws Exception {

        String tokenUrl = "https://token-url";
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String jwtToken = "jwt_token";
        String responseJson = "{\"access_token\":\"access_token_value\",\"expires_in\":3600}";

        try (MockedStatic<RequestBuilder> mockedRequestBuilder = mockStatic(RequestBuilder.class);
             MockedStatic<HttpClientBuilder> mockedHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedStatic<IOUtils> mockedIOUtils = mockStatic(IOUtils.class)) {
            mockedRequestBuilder.when(() -> RequestBuilder.post()).thenReturn(requestBuilder);
            mockedHttpClientBuilder.when(() -> HttpClientBuilder.create()).thenReturn(clientBuilder);
            mockedIOUtils.when(()->IOUtils.toString(eq(inputStream), eq(StandardCharsets.UTF_8))).thenReturn(responseJson);

            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            when(entity.getContent()).thenReturn(inputStream);

            SimpleOAuthClient client = new SimpleOAuthClient(tokenUrl, clientId, clientSecret);
            String content = client.getContent(jwtToken, "");
            assertNotNull(content);
        }
    }
}
