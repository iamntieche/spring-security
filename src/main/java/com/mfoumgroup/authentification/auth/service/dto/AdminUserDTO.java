package com.mfoumgroup.authentification.auth.service.dto;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.mfoumgroup.authentification.auth.domain.AuthorityEntity;
import com.mfoumgroup.authentification.auth.domain.UserEntity;

import lombok.Data;

@Data
public class AdminUserDTO extends UserDTO {

    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated = false;
    private String langKey;
    private String imageUrl;
    private String activationKey;
    private String resetKey;
    private Instant resetDate = null;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private Set<String> authorities;
    
    public AdminUserDTO(UserEntity user){

        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream().map(AuthorityEntity::getName).collect(Collectors.toSet());
    }
    
}
