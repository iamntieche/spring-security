package com.adservio.authentification.auth.security.jwt;

import com.adservio.authentification.auth.security.configuration.AuthProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    private static final String AUTHORITIES_KEY = "auth";


    private final AuthProperties authProperties;

    private final JwtParser jwtParser;
    private Key key;

    public TokenProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;


        byte[] keyBytes ;
        String secret = authProperties.getJwt().getBase64Secret();
        if (checkForEncode(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes= Decoders.BASE64.decode(secret);
        }else{
            log.warn(
                    "Warning: the JWT key used is not Base64-encoded. "
            );
            secret = authProperties.getJwt().getSecretKey();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);


        }
        key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliseconds = 1000 * authProperties.getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
                1000 * authProperties.getJwt().getTokenValidityInSecondsForRememberMe();

    }

    public boolean checkForEncode(String base64String) {
        if(base64String == null || base64String.isEmpty() || base64String.isBlank()){
            return false;
        }
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(base64String);
        boolean value = true;
        if (!m.find()) {
           value = false;
        }
        return value;
    }
    public boolean validateToken(String authToken) {
        boolean value = true;
        try {
            jwtParser.parseClaimsJws(authToken);
        }catch (ExpiredJwtException e){
            value = false;
            log.error(e.getMessage());
        }catch (SignatureException e){
            value = false;
            log.error(e.getMessage());
        }catch (MalformedJwtException e){
            value = false;
            log.error(e.getMessage());
        }catch (IllegalArgumentException e){
            value = false;
            log.error(e.getMessage());
        }
        return value;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            long now = (new Date()).getTime();
            Date validity  = new Date(now + this.tokenValidityInMilliseconds);
            if(rememberMe) {
                validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
            }
        return Jwts.builder().setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
}
