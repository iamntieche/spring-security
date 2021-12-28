package com.mfoumgroup.authentification.auth.security.jwt;

import com.mfoumgroup.authentification.auth.security.configuration.AuthProperties;
import com.mfoumgroup.authentification.auth.util.AuthoritiesConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import  static org.assertj.core.api.Assertions.assertThat;


import java.util.Collections;

public class JWTFilterTest {

    public TokenProvider tokenProvider;

    public JWTFilter jwtFilter;

    @BeforeEach
    public void setup(){
        AuthProperties authProperties = new AuthProperties();
        String base64Secret = "Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
        String secretKey = "Bfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
        authProperties.getJwt().setSecretKey(secretKey);
        authProperties.getJwt().setBase64Secret(base64Secret);
        tokenProvider = new TokenProvider(authProperties);

        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInSeconds", 86400);
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInSecondsForRememberMe", 2592000);

        jwtFilter = new JWTFilter(tokenProvider);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
    @Test
    void testJWTFilter() throws  Exception{
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test-user", "test-password", Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));

        String jwt = tokenProvider.createToken(authentication,false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer "+jwt);
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilterInternal(request,response,filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("test-user");
    }

    @Test
    void testJWTFilterInvalidToken() throws Exception {
        String jwt = "wrong_jwt";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testJWTFilterMissingAuthorization() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testJWTFilterMissingToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer ");
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testJWTFilterWrongScheme() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "test-user",
                "test-password",
                Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER))
        );
        String jwt = tokenProvider.createToken(authentication, false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Basic " + jwt);
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
