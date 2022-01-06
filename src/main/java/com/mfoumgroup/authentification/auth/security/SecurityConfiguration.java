package com.mfoumgroup.authentification.auth.security;

import com.mfoumgroup.authentification.auth.constant.AuthoritiesConstants;
import com.mfoumgroup.authentification.auth.security.configuration.AuthProperties;
import com.mfoumgroup.authentification.auth.security.jwt.JWTConfigurer;
import com.mfoumgroup.authentification.auth.security.jwt.TokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public final AuthProperties authProperties;
    private final TokenProvider tokenProvider;

    private static final String CONTENTSECURITYPOLICY = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

    @Autowired
    private UserDetailsService userDetailsService;

    //cryptage par excellence des mots de passe.
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityConfiguration(AuthProperties authProperties, TokenProvider tokenProvider) {
        this.authProperties = authProperties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/h2-console/**")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/test/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable();
        http.headers().contentSecurityPolicy(CONTENTSECURITYPOLICY)
                .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
                .and().frameOptions().deny()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/activate").permitAll()
                .antMatchers("/api/account/reset-password/init").permitAll()
                .antMatchers("/api/account/reset-password/finish").permitAll()
                .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/health/**").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/prometheus").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .and()
                .httpBasic()
                .and()
                .apply(securityConfigurerAdapter());

    }

    private JWTConfigurer securityConfigurerAdapter(){
        return new JWTConfigurer(tokenProvider);
    }
}
