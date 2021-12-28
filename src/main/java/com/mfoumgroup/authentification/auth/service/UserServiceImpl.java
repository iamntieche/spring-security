package com.mfoumgroup.authentification.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.mfoumgroup.authentification.auth.domain.AuthorityEntity;
import com.mfoumgroup.authentification.auth.domain.UserEntity;
import com.mfoumgroup.authentification.auth.exception.EmailAlreadyUsedException;
import com.mfoumgroup.authentification.auth.exception.InvalidPasswordException;
import com.mfoumgroup.authentification.auth.exception.LoginAlreadyUsedException;
import com.mfoumgroup.authentification.auth.repository.AuthorityRepository;
import com.mfoumgroup.authentification.auth.repository.UserRepository;
import com.mfoumgroup.authentification.auth.security.SecurityUtils;
import com.mfoumgroup.authentification.auth.dto.UserDTO;
import com.mfoumgroup.authentification.auth.mapper.UserMapper;
import com.mfoumgroup.authentification.auth.util.AuthoritiesConstants;
import com.mfoumgroup.authentification.auth.util.ConstantsUtils;
import com.mfoumgroup.authentification.auth.util.RandomUtil;


import com.mfoumgroup.authentification.auth.dto.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    public Optional<UserDTO> requestPasswordReset(String email) {

        return userRepository.findOneByEmailIgnoreCase(email)
        .filter(UserEntity::getActivated)
        .map(user -> {
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(Instant.now());
            log.debug("email {} ",email);
            return user;
        }).map(UserDTO::new);
    }

    @Override
    public UserDTO saveUser(UserEntity user) {
        return userMapper.userToUserDTO(userRepository.saveAndFlush(user));
    }

    @Override
    public void deleteUser(String login) {
        userRepository
                .findOneByLogin(login)
                .ifPresent(user -> {
                    userRepository.delete(user);
                    log.debug("Deleted User: {}", user);
                });
    }

    @Override
    public Optional<UserDTO> completePasswordReset(String newPassword, String resetKey) {
        log.debug("Reset user password for reset key {} ", resetKey);
        return userRepository.findOneByResetKey(resetKey)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
                .map(user ->{
                   user.setPassword(passwordEncoder.encode(newPassword));
                   user.setResetKey(null);
                   user.setResetDate(null);
                   return user;
                }).map(UserDTO::new);
    }

    @Override
    public List<UserDTO> findAllByActivatedIsFalseAndCreatedDateBefore(Instant minus) {

        return userMapper.usersToUserDTOs(userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(minus));
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<UserEntity> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));

        for (UserEntity user: users){
            log.debug("Deleting not activated user {} ", user.getLogin());
            userRepository.delete(user);
        }
    }

    @Override
    public  Optional<UserDTO> findOneByLogin(String anonymousUser) {
        return userRepository.findOneByLogin(anonymousUser).map(UserDTO::new);
    }

    @Override
    public int count() {
        return (int) userRepository.count();
    }



    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUser(String anonymousUser, Pageable pageable) {

        return userRepository.findAllByLoginNot(anonymousUser, pageable).map(UserDTO::new);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity user = new UserEntity();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(ConstantsUtils.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        if (userDTO.getAuthorities() != null) {
            Set<AuthorityEntity> authorities = userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return userMapper.userToUserDTO(user);
    }

    @Override
    public Optional<UserEntity> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    @Override
    public UserDTO registerUser(ManagedUserVM userDTO) {
        userRepository
                .findOneByLogin(userDTO.getLogin().toLowerCase())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new LoginAlreadyUsedException();
                    }
                });

        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                });
        UserEntity newUser = new UserEntity();
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<AuthorityEntity> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);

        log.debug("Created Information for User: {}", newUser);
        return userMapper.userToUserDTO(newUser);
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional
                .of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    user.setImageUrl(userDTO.getImageUrl());
                    user.setActivated(userDTO.getActivated());
                    user.setLangKey(userDTO.getLangKey());
                    Set<AuthorityEntity> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO
                            .getAuthorities()
                            .stream()
                            .map(authorityRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(managedAuthorities::add);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                })
                .map(UserDTO::new);
    }

    @Override
    public Optional<UserDTO> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
                .findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    log.debug("Activated user: {}", user);
                    return user;
                }).map(UserDTO::new);
    }

    @Override
    public void changePassword(String currentPassword, String newPassword) {
        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    userRepository.save(user);
                    log.debug("Changed password for User: {}", user);
                });
    }

    @Override
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    @Override
    public Optional<UserEntity> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    if (email != null) {
                        user.setEmail(email.toLowerCase());
                    }
                    user.setLangKey(langKey);
                    user.setImageUrl(imageUrl);
                    userRepository.save(user);
                    log.debug("Changed Information for User: {}", user);
                });
    }


    private boolean removeNonActivatedUser(UserEntity existingUser) {
        if (existingUser.getActivated() == true) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

}
