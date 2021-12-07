package com.adservio.authentification.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.repository.UserRepository;
import com.adservio.authentification.auth.service.dto.UserDTO;
import com.adservio.authentification.auth.service.mapper.UserMapper;
import com.adservio.authentification.auth.util.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
    public void deleteUser(UserDTO user) {
        userRepository.deleteById(user.getId());
    }

    @Override
    public Optional<UserDTO> completePasswordReset(String newPassword, String resetKey) {
        log.debug("Reset user password for reset key {} ", resetKey);
        return userRepository.findOneByResetKey(resetKey)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
                .map(user ->{
                   user.setPassword(newPassword);
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


}
