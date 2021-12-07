package com.adservio.authentification.auth.service.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.adservio.authentification.auth.domain.AuthorityEntity;
import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.service.dto.AdminUserDTO;
import com.adservio.authentification.auth.service.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
/**
 * Class user mapper
 */
public class UserMapper {

    public List<UserDTO> usersToUserDTOs(List<UserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
    }

    public UserDTO userToUserDTO(UserEntity user) {
     return new UserDTO(user);
    }

    public UserEntity userDtoToUser(AdminUserDTO userDTO) {
        if(userDTO == null){
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            user.setAuthorities(authoritiesFromStrings(userDTO.getAuthorities()));
         return user;   
    }

    public List<UserEntity> usersDtoToUsers(List<AdminUserDTO> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDtoToUser).collect(Collectors.toList());
    }

    private Set<AuthorityEntity> authoritiesFromStrings(Set<String> authoritiesAsString){
        Set<AuthorityEntity> authorities = new HashSet<>();

        if(authoritiesAsString != null){
            authorities = authoritiesAsString
            .stream()
            .map(string -> {
                AuthorityEntity auth = new AuthorityEntity();
                auth.setName(string);
                return auth;
            })
            .collect(Collectors.toSet());
        }
        return authorities;
    }

    public UserEntity userFromId(Long id) {
        if (id == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
    
}

