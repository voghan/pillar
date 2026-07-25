package com.voghan.pillar.client.config.impl;

import com.voghan.pillar.client.config.OAuthConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class LocalPublishConfigTest {

    @Test
    void getName_returnsName() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getName());
    }

    @Test
    void getClientId_returnsClientId() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getClientId());
    }

    @Test
    void getClientSecret_returnsClientSecret() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getClientSecret());
    }

    @Test
    void getRedirectUrl_returnsRedirectUrl() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getRedirectUrl());
    }

    @Test
    void getScope_returnScope() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getScope());
    }

    @Test
    void getOauthServer_returnOauthServer() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getOauthServer());
    }

    @Test
    void getOauthTokenEndpoint_returnOauthTokenEndpoint() {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.getOauthTokenEndpoint());
    }

    @Test
    void createJWT_returnJWT() throws InvalidKeySpecException, NoSuchAlgorithmException {
        OAuthConfig config = new LocalPublishConfig();
        assertNotNull(config.createJWT());
    }
}
