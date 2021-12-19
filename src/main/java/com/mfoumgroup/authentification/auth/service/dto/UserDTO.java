package com.mfoumgroup.authentification.auth.service.dto;

import com.mfoumgroup.authentification.auth.domain.UserEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean activated;
    private String langKey;
    private String imageUrl;
    private String activationKey;
    private String resetKey;
    private Instant resetDate;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    public UserDTO (UserEntity user){
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.langKey = user.getLangKey();
        this.imageUrl = user.getImageUrl();
        this.resetDate = user.getResetDate();
        this.resetKey = user.getResetKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.password = user.getPassword();
    }
}
