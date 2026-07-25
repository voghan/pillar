package com.voghan.pillar.client.config;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface OAuthConfig {
    String getClientId();

    String getClientSecret();

    String getName();

    String getRedirectUrl();

    String getScope();

    String getOauthServer();

    String getOauthTokenEndpoint();

    String createJWT() throws InvalidKeySpecException, NoSuchAlgorithmException;
}
