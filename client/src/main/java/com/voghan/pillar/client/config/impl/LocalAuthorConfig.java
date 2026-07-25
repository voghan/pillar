package com.voghan.pillar.client.config.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.voghan.pillar.client.config.OAuthConfig;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

public class LocalAuthorConfig implements OAuthConfig {
    private static final String clientId;
    private static final String clientSecret;
    private static final String name;
    private static final String redirectUrl;
    private static final String scope;
    private static final String privetKey;
    private static final String oauthServer;
    private static final String oauthTokenEndpoint;

    static {
        clientId = "8quq772o21u4aqr5ja9ob4dsij-lqtpgrtd";
        clientSecret = "2hkfvrpmrl4be109ckpfs4gnm7";
        name = "Pillar";
        redirectUrl = "http://localhost:8080/pillar";
        scope = "pillar_assets_read,pillar_site_read,pillar_conf_read,pillar_graphql";
        oauthServer = "http://localhost:4502";
        oauthTokenEndpoint = oauthServer + "/oauth/token";

        privetKey =
            "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCcAswEB/PbXG4o" +
            "MBJ6JoM9f1w9deSCiyhGKq8BtNe6tv4h4T6Ew9JMuNgp8zIu9D1lhQW2z5CrYRsq" +
            "ZZXi37U2hEWR9SARt0koI4a876EWcgRMfl/LX15pyNWEGtg67SRUfrNuJGoqk03Y" +
            "gzh0QjyJyuwfO42YGBWPDwFkBQnxNQ6IxhuhWzeX0HZIfkS5f59smMJdFMz9Dxy1" +
            "fYmXnaAagfJy9vCGdGDwEB6KWNB42V4wFLaRVLiMWAYM6p1jgZsUe6VDg2XId+wP" +
            "q9QlaoG9/tsJ6ViHE9PLcOZoBa7sTPRo8RsV56Jgs6YJ35ysn8llQ3iAw2TMUQvQ" +
            "CYrnFJx1AgMBAAECggEAJBvPnYU7r0Ni6aFKvYPpb1PphP4i4AYWUbsyW0pAfiI5" +
            "ipzoC/GI3KIRwg/cRxrXlQYnTusWuFvW3ka53NNbTV1q/r/F5uPNpmEmn9c0qQ3F" +
            "qfC7+kW+A+zsYlGWR3k4pefXXsBF5EnIxDqG1/22pkvVv9rYSGIWp1BZSY2TAgUh" +
            "PACaiSoCexeAFh3H/vpFN7EXvUBx7f00MC4km/77HS6UnR3HeH9QBjQBZaLNM5wI" +
            "BOU7m9bTrMjNlWef5lapPY3+bMar1goRc58/Jru42aoMMRxZo2Fg6V7yGLaNSML0" +
            "DJLJ06auNQ7tLWiqDpr2VUij6UkmGlH6GrohhWVrhwKBgQDcnxuBzNxRAhRRpUU3" +
            "AEkQ/DnHYmNP8jAMwCpt0fI9Hd7Gg4kUAGE4SHHQktyiujlJ010PnbiLWcw9jmrh" +
            "JNurBtKEwRO7dwh+vcnNy1vxMH0+yaEa5WnyWnkk5MZWuux21ldXsI2S7zyGiEQZ" +
            "FnJxIV5pLjFE9NHJ5LHPcGV7UwKBgQC1B1BQ+cJwuwRH/BMR4RslDPfc0pLJNbhR" +
            "aNniS36pGZY0fRa3E8dJm2PzSs/zaRadEVk5jZsnvoiJXKYB5aVYflhPuh7Y9npU" +
            "bEZ7JDrF08JE1WYgOCOtds9G2xcTadogeERGK0LBZkpaRmnuAkqYA6ToIp0SMFHg" +
            "VSQiheZYFwKBgQCAXAnzATAF6cHMpu2YTJA2U6mCOdoyP2R5ZqzI7GeV63Ub5pew" +
            "jXADARNHGMtJ2jNOoqz4Uvl2Ci7seZnxmDI+VV/SYUuLvHuy0dVB6Nl7gIy+JS2Y" +
            "qnVzzfPB5WEIQQUnNxnmKRCVtp9uXoXQWuEGtTVq2LdNuWr9nhNd77T7hQKBgQCb" +
            "oTwFQZNOAxm9IRLr7hC+G5YDe+zBmrNpGbEngSvVCLA/U8LxhclwmGynPovkgVX1" +
            "lnKUrg7TblRHcyUswFcT6xrU//eBIvUsGyHEv/LfHBwJvURdMqZJfLjZ+4StJfVm" +
            "sL5RbOOpDN2jGqH7AnFKmudswkUy2+On7spv+5l55QKBgQCGywJGNkOs611jSXiQ" +
            "mkgm3PNJkT8s9CLqszkGbfTi9Epei4hW5652aG2EcTjcd4PMVD9szhDNduMYdFQY" +
            "UKMJWqQZNhkeQGdeT38/x2YapiCrFDv8aZsqCn/3RGZpUWDMZlRo3tScA2128lAn" +
            "YQUdxvqlCLe7i0QMx5bkU1tK2Q==";
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getOauthServer() {
        return oauthServer;
    }

    @Override
    public String getOauthTokenEndpoint() {
        return oauthTokenEndpoint;
    }

    public String createJWT() throws InvalidKeySpecException, NoSuchAlgorithmException {
        PrivateKey privateKey = getPrivateKey();
        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) privateKey);
        return JWT.create()
                .withIssuer(clientId)
                .withAudience(oauthTokenEndpoint)
                .withClaim("scope", scope)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(algorithm);
    }

    private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] encoded = Base64.decodeBase64(privetKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
