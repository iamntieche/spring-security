package com.mfoumgroup.authentification.auth.security.jwt;

import com.mfoumgroup.authentification.auth.constant.AuthoritiesConstants;
import com.mfoumgroup.authentification.auth.security.configuration.AuthProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TokenProviderTest {
    private static final long ONE_MINUTE = 60000;
    private TokenProvider tokenProvider;
    private Key key;

    @BeforeEach
    public void setup(){
        AuthProperties authProperties = new AuthProperties();
        String base64Secret = "Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
        String secretKey = "Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
        authProperties.getJwt().setSecretKey(secretKey);
        authProperties.getJwt().setBase64Secret(base64Secret);
        tokenProvider = new TokenProvider(authProperties);
        ReflectionTestUtils.setField(tokenProvider, "key", key);
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInSeconds", ONE_MINUTE);

    }

    @Test
    void testReturnTrueWhenJWThasValidSignature(){
        boolean isTokenValid = tokenProvider.validateToken(createTokenWithSameSignature());
        assertThat(isTokenValid).isTrue();
    }

    @Test
   void testReturnFalseWhenJWThasInvalidSignature(){
        String signInKey = createTokenWithDifferentSignature();
        boolean isTokenValid = tokenProvider.validateToken(signInKey);
        assertThat(isTokenValid).isFalse();
   }
   @Test
   void testReturnFalseWhenJWTisMalformed(){
       Authentication authentication = createAuthentication();
       String token = tokenProvider.createToken(authentication, false);
       String invalidToken = token.substring(1);
       boolean isTokenValid = tokenProvider.validateToken(invalidToken);
       assertThat(isTokenValid).isFalse();
   }
   @Test
   void testReturnFalseWhenJWTisExpired(){
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE);
        Authentication  authentication = createAuthentication();

        String token = tokenProvider.createToken(authentication, false);
        boolean isTokenVlid = tokenProvider.validateToken(token);

        assertThat(isTokenVlid).isFalse();
   }
    @Test
   void testReturnFalseWhenJWTisUnsupported(){
        String unsupportedToken = createUnsupportedToken();

        boolean isTokenValid = tokenProvider.validateToken(unsupportedToken);

        assertThat(isTokenValid).isFalse();
   }
   @Test
   void testReturnFalseWhenJWTisInvalid(){
        boolean isTokenValid = tokenProvider.validateToken("");

        assertThat(isTokenValid).isFalse();
   }
    @Test
   void testKeyIsSetFromSecretWhenSecretIsNotEmpty(){
        final String secret = "NwskoUmKHZtzGRKJKVjsJF7BtQMMxNWi";
        AuthProperties authProperties = new AuthProperties();
        authProperties.getJwt().setSecretKey(secret);

        TokenProvider tokenProvider = new TokenProvider(authProperties);

        Key key = (Key) ReflectionTestUtils.getField(tokenProvider, "key");
        assertThat(key).isNotNull().isEqualTo(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)));
   }

   @Test
   void testKeyIsSetFromBase64SecretWhenSecretIsEmpty(){
        final  String base64Secret = "Z3phdXphZmRudi1iZGJzYmRocXZzZFFTS1FTREpRNkRGQkhRU0JERkJRVVNWRkpRU0pxcy1rZHNxa3NkcWprZGpzcWtkbHFzZGxrc3Fkw7lx";
        AuthProperties authProperties = new AuthProperties();
        authProperties.getJwt().setBase64Secret(base64Secret);
        TokenProvider tokenProvider = new TokenProvider(authProperties);
        Key key = (Key) ReflectionTestUtils.getField(tokenProvider,"key");
        assertThat(key).isNotNull().isEqualTo(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(base64Secret)));
   }
    private String createUnsupportedToken() {
        return Jwts.builder().setPayload("payload").signWith(key, SignatureAlgorithm.HS512).compact();
    }

    private Authentication createAuthentication() {
        Collection<GrantedAuthority>  authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }

    private String createTokenWithDifferentSignature() {
        Key ortherKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode("Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        );
        return Jwts.builder().setSubject("anonymous")
                .signWith(ortherKey, SignatureAlgorithm.HS512)
                .setExpiration(new Date(new Date().getTime() + ONE_MINUTE))
                .compact();
    }
    private String createTokenWithSameSignature() {
        Key ortherKey = Keys.hmacShaKeyFor("Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8".getBytes(StandardCharsets.UTF_8));

        return Jwts.builder().setSubject("anonymous")
                .signWith(ortherKey, SignatureAlgorithm.HS512)
                .setExpiration(new Date(new Date().getTime() + ONE_MINUTE))
                .compact();
    }

}
