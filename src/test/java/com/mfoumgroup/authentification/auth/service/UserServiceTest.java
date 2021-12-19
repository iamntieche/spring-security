package com.mfoumgroup.authentification.auth.service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.mfoumgroup.authentification.auth.IntegrationTest;
import com.mfoumgroup.authentification.auth.config.Constants;
import com.mfoumgroup.authentification.auth.domain.UserEntity;

import com.mfoumgroup.authentification.auth.service.dto.AdminUserDTO;
import com.mfoumgroup.authentification.auth.service.dto.UserDTO;
import com.mfoumgroup.authentification.auth.service.mapper.UserMapper;
import com.mfoumgroup.authentification.auth.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource Rest Controller
 */
@Transactional
@IntegrationTest
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    private UserEntity user;

    @BeforeEach
    public void init(){

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
        userService.saveUser(user);
        Optional<UserDTO> maybeUser = userService.requestPasswordReset("invalid.login@localhost");
        assertThat(maybeUser).isNotPresent();

        maybeUser = userService.requestPasswordReset(user.getEmail());
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @Transactional
    public void assertThatOnlyActivatedUserCanRequestPasswordReset(){
        user.setActivated(false);
        userService.saveUser(user);

        Optional<UserDTO> existUser = userService.requestPasswordReset(user.getEmail());
        assertThat(existUser).isNotPresent();
        userService.deleteUser(userMapper.userToUserDTO(user));
    }
    @Test
    @Transactional
    public void assertThatResetKeyMustNotBeOlderThan24Hours(){
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userService.saveUser(user);

        Optional<UserDTO> existUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(existUser).isNotPresent();
        userService.deleteUser(new AdminUserDTO(user));
    }
    @Test
    @Transactional
    public void assertThatKeyMustBeValid(){
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        userService.saveUser(user);

        Optional<UserDTO> existUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(existUser).isNotPresent();
        userService.deleteUser(new AdminUserDTO(user));
    }

    @Test
    @Transactional
    public void assertThatUserCanResetPassword(){
        Instant daysAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        String password = RandomUtil.generatePassword();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userService.saveUser(user);

        Optional<UserDTO> existUser  = userService.completePasswordReset(password, user.getResetKey());
        assertThat(existUser).isPresent();
        assertThat(existUser.orElse(null).getPassword()).isEqualTo(password);
        assertThat(existUser.orElse(null).getResetDate()).isNull();
       assertThat(existUser.orElse(null).getResetKey()).isNull();
        userService.deleteUser(userMapper.userToUserDTO(user));
    }



    @Test
    @Transactional
    public void testFindNotActivatedUsersByCreationDateBefore(){
        Instant now = Instant.now();
        user.setActivated(false);
        UserDTO dbUser = userService.saveUser(user);
        dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
        userService.saveUser(user);
        List<UserDTO> users = userService.findAllByActivatedIsFalseAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
        assertThat(users).isNotNull();
        userService.removeNotActivatedUsers();
        users = userService.findAllByActivatedIsFalseAndCreatedDateBefore(now);
        assertThat(users).isNotNull();
    }

    @Test
    public  void assertThatAnonymousUserIsNotGet(){
        user.setLogin(Constants.ANONYMOUS_USER);
        if(!userService.findOneByLogin(Constants.ANONYMOUS_USER).isPresent()){
            userService.saveUser(user);
        }
        final Pageable pageable = PageRequest.of(0, userService.count());
        final Page<UserDTO> users = userService.getAllManagedUser(user.getLogin(),pageable);
        assertThat(users.getContent().stream()
                .noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getLogin())))
                .isTrue();
    }

    @Test
    @Transactional
    public void testRemoveNotActivatedUsers() {
        user.setActivated(false);
        userService.saveUser(user);
        // Let the audit first set the creation date but then update it
        user.setCreatedDate(Instant.now().minus(30, ChronoUnit.DAYS));
        userService.saveUser(user);

        assertThat(userService.findOneByLogin("johndoe")).isPresent();
        userService.removeNotActivatedUsers();
        assertThat(userService.findOneByLogin("johndoe")).isNotPresent();
    }

}
