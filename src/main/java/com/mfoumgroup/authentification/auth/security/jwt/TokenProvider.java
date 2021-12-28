package com.mfoumgroup.authentification.auth.security.jwt;

import com.mfoumgroup.authentification.auth.security.configuration.AuthProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private final long tokenValidityInSeconds;

    private final long tokenValidityInSecondsForRememberMe;

    private static final String AUTHORITIES_KEY = "auth";


    public final AuthProperties authProperties;

    private final JwtParser jwtParser;
    private final Key key;

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
        this.tokenValidityInSeconds =  authProperties.getJwt().getTokenValidityInSeconds();
        this.tokenValidityInSecondsForRememberMe =
                authProperties.getJwt().getTokenValidityInSecondsForRememberMe();

    }


    public boolean validateToken(String authToken) {
        boolean value = true;
        try {
            jwtParser.parseClaimsJws(authToken);
        }catch (ExpiredJwtException | SignatureException | MalformedJwtException | IllegalArgumentException e){
            value = false;
            log.error(e.getMessage());
        }
        return value;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            long now = (new Date()).getTime();
            Date validity  = new Date(now + this.tokenValidityInSeconds *1000 );
            if(rememberMe) {
                validity = new Date(now + this.tokenValidityInSecondsForRememberMe * 1000);
            }
        return Jwts.builder().setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean checkForEncode(String base64String) {
        if(base64String == null || base64String.isEmpty() || base64String.isBlank()){
            return false;
        }
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(base64String);
        return m.find();
    }
}
