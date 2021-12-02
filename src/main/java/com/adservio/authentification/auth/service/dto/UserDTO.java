package com.adservio.authentification.auth.service.dto;

import com.adservio.authentification.auth.domain.UserEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String login;

    public UserDTO (UserEntity user){
        this.id = user.getId();
        this.login = user.getLogin();
    }
}
