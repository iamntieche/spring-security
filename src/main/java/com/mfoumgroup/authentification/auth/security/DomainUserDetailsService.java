package com.mfoumgroup.authentification.auth.security;

import com.mfoumgroup.authentification.auth.UserNotActivatedException;
import com.mfoumgroup.authentification.auth.domain.UserEntity;
import com.mfoumgroup.authentification.auth.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import   org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        log.debug("Authenticating {}", login);
        EmailValidator validator = EmailValidator.getInstance();
        if (validator.isValid(login)) {
            String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
            return userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin)
                    .map(userEntity -> createSpringSecurityuser(lowercaseLogin, userEntity))
                    .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
        } else {
            return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(login)
                    .map(userEntity -> createSpringSecurityuser(login, userEntity))
                    .orElseThrow(() -> new UserNotActivatedException("User with email " + login + " was not found in the database"));
        }


    }

    private User createSpringSecurityuser(String lowercaseLogin, UserEntity user){
        if(!user.getActivated()){
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getName()))
                .collect(Collectors.toList());
        return new User(user.getLogin(), user.getPassword(), grantedAuthorities);
    }
}
