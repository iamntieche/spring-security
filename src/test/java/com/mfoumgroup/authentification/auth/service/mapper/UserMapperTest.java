package com.mfoumgroup.authentification.auth.service.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mfoumgroup.authentification.auth.domain.UserEntity;
import com.mfoumgroup.authentification.auth.dto.UserDTO;

import com.mfoumgroup.authentification.auth.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private UserMapper userMapper;
    private UserEntity user;
    private UserDTO userDto;

    @BeforeEach
    public void init(){

        userMapper = new UserMapper();
        user = new UserEntity();
       
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        userDto = new UserDTO(user);
    }

    @Test
    void userToUserDTOShouldOnlyNonNull(){
        //given
        UserDTO newUserDto;
        //when
        newUserDto = userMapper.userToUserDTO(user);
        //then
        assertThat(newUserDto.getId()).isEqualTo(user.getId());
        assertThat(newUserDto.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    void userDtoToUserShouldOnlyNonNull(){
        //given
        UserEntity user1;
        //when
        user1 = userMapper.userDtoToUser(userDto);
        //then
        assertThat(user1.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void usersToUserDTOsShouldMapOnlyNonNullUsers(){

        //given
        List<UserEntity> users = new ArrayList<>();
        users.add(user);
        users.add(null);
        //when
        List<UserDTO> userDTOs = userMapper.usersToUserDTOs(users);
        //then
        assertThat(userDTOs).isNotEmpty().size().isEqualTo(1);
    
    }
    @Test
    void usersDtoToUsersShouldMapOnlyNonNullUsers(){
        //given
        List<UserDTO> userDTOs = new ArrayList<>();
        userDTOs.add(userDto);
        userDTOs.add(null);
        //when
        List<UserEntity> users = userMapper.usersDtoToUsers(userDTOs);

        assertThat(users).isNotEmpty().size().isEqualTo(1);
    }
    @Test
    void userDtosToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain(){
        //given
        Set<String> authoritiesAsString = new HashSet<>();
        authoritiesAsString.add("ADMIN");
        userDto.setAuthorities(authoritiesAsString);
        List<UserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        //when
        List<UserEntity> users = userMapper.usersDtoToUsers(usersDto);
        //then
        assertThat(users).isNotEmpty().size().isEqualTo(1);
        assertThat(users.get(0).getAuthorities()).isNotNull();
        assertThat(users.get(0).getAuthorities()).isNotEmpty();
        assertThat(users.get(0).getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
    }

    @Test
    void userDtosToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities(){
        userDto.setAuthorities(null);

        List<UserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);

        List<UserEntity> users = userMapper.usersDtoToUsers(usersDto);

        assertThat(users).isNotEmpty().size().isEqualTo(1);
        assertThat(users.get(0).getAuthorities()).isNotNull();
        assertThat(users.get(0).getAuthorities()).isEmpty();
    }

    @Test
    void userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
        Set<String> authoritiesAsString = new HashSet<>();
        authoritiesAsString.add("ADMIN");
        userDto.setAuthorities(authoritiesAsString);

        UserEntity user = userMapper.userDtoToUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAuthorities()).isNotEmpty();
        assertThat(user.getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
    }

    @Test
    void userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.setAuthorities(null);

        UserEntity user = userMapper.userDtoToUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void userDTOToUserMapWithNullUserShouldReturnNull() {
        assertThat(userMapper.userDtoToUser(null)).isNull();
    }

    @Test
    void testUserFromId() {
        assertThat(userMapper.userFromId(DEFAULT_ID).getId()).isEqualTo(DEFAULT_ID);
        assertThat(userMapper.userFromId(null)).isNull();
    }
}
