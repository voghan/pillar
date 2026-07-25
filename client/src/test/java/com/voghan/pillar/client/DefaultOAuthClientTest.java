package com.voghan.pillar.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultOAuthClientTest {

    @Mock
    HttpClient httpClient;

    @Mock
    HttpResponse response;

    @Test
    void main_executeMock() {
        String[] arguments = {};

        DefaultOAuthClient.TokenResponse tokenResponse = mock(DefaultOAuthClient.TokenResponse.class);

        try (MockedConstruction<DefaultOAuthClient> mockedClient = mockConstruction(
                DefaultOAuthClient.class,
                (mock, context) -> when(mock.requestAccessToken(any(), any())).thenReturn(tokenResponse))) {
            assertDoesNotThrow(() -> DefaultOAuthClient.main(arguments));
        }
    }

    @Test
    void requestAccessToken_returns200Status() throws Exception {

        String tokenUrl = "https://token-url";
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String scope = "test_scope";
        String jwtToken = "jwt_token";
        String responseJson = "{\"access_token\":\"access_token_value\",\"expires_in\":3600}";

        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(() -> HttpClient.newHttpClient()).thenReturn(httpClient);
            when(httpClient.send(any(), any())).thenReturn(response);
            when(response.statusCode()).thenReturn(200);
            when(response.body()).thenReturn(responseJson);

            DefaultOAuthClient client = new DefaultOAuthClient(tokenUrl, clientId, clientSecret);
            DefaultOAuthClient.TokenResponse tokenResponse = client.requestAccessToken(scope, jwtToken);
            assertNotNull(tokenResponse);
            assertEquals("access_token_value", tokenResponse.accessToken());
        }

    }

    @Test
    void requestAccessToken_returns401Status() throws Exception {

        String tokenUrl = "https://token-url";
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String scope = "test_scope";
        String jwtToken = "jwt_token";
        String responseJson = "{\"access_token\":\"access_token_value\",\"expires_in\":3600}";

        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(() -> HttpClient.newHttpClient()).thenReturn(httpClient);
            when(httpClient.send(any(), any())).thenReturn(response);
            when(response.statusCode()).thenReturn(401);
            when(response.body()).thenReturn(responseJson);

            DefaultOAuthClient client = new DefaultOAuthClient(tokenUrl, clientId, clientSecret);
            Exception exception = Assertions.assertThrows(
                    Exception.class,
                    () -> client.requestAccessToken(scope, jwtToken)
            );

            Assertions.assertEquals("Token request failed: 401 " + responseJson, exception.getMessage());
        }

    }
}
