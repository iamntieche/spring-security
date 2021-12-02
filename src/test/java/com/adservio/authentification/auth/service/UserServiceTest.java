package com.adservio.authentification.auth.service; 
import java.util.Optional;

import com.adservio.authentification.auth.IntegrationTest;
import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.repository.UserRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource Rest Controller
 */
@Transactional
@IntegrationTest
public class UserServiceTest {

    private UserRepository userRepository;

    private UserService userService;

    private UserEntity user;

    @BeforeEach
    public void init(){
        userRepository = Mockito.mock(UserRepository.class);
       
        user = new UserEntity();
        user.setLogin("johndoe");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("http://placehold.it/50x50");
        user.setLangKey("en");
    }
    
    @Test
    @Transactional
    public void assertThatUserMustExistToResetPassword(){
        userRepository.saveAndFlush(user);
        Optional<UserEntity> maybeUser = userService.requestPasswordReset("invalid.login@localhost");
        assertThat(maybeUser).isNotPresent();
    }
}
