package com.adservio.authentification.auth.security.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "authenticationProps")
public class AuthProperties {

    private final JWT jwt = new JWT();

    public JWT getJwt() {
        return jwt;
    }

    public static class JWT{
        private String secretKey;
        private String base64Secret;
        private long tokenValidityInSeconds;
        private long tokenValidityInSecondsForRememberMe;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBase64Secret() {
            return base64Secret;
        }

        public void setBase64Secret(String base64Secret) {
            this.base64Secret = base64Secret;
        }

        public long getTokenValidityInSeconds() {
            return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
            this.tokenValidityInSeconds = tokenValidityInSeconds;
        }

        public long getTokenValidityInSecondsForRememberMe() {
            return tokenValidityInSecondsForRememberMe;
        }

        public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
            this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
        }

    }


}


