package com.mfoumgroup.authentification.auth.dto;

import com.mfoumgroup.authentification.auth.domain.AuthorityEntity;
import com.mfoumgroup.authentification.auth.domain.UserEntity;

import com.mfoumgroup.authentification.auth.util.ConstantsUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    @NotBlank
    @Pattern(regexp = ConstantsUtils.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;
    private String password;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @Email
    @Size(min = 5, max = 254)
    private String email;
    @Column(nullable = false)
    private Boolean activated;
    @Size(min = 2, max = 10)
    private String langKey;
    private String imageUrl;
    private String activationKey;
    private String resetKey;
    private Instant resetDate;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Set<String> authorities;

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
        this.authorities = user.getAuthorities().stream().map(AuthorityEntity::getName).collect(Collectors.toSet());

    }
}
