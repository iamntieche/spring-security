package com.mfoumgroup.authentification.auth.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mfoumgroup.authentification.auth.util.ConstantsUtils;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

 
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity(name = "User")
@Table(name = "user")
@ToString
public class UserEntity extends AbstractAuditingEntity  {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = ConstantsUtils.LOGIN_REGEX)
    @Size(min = 5, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 5, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean activated;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private Instant resetDate;

    //un commentaire
    @ManyToMany
     private Set<AuthorityEntity> authorities = new HashSet<>();

    public UserEntity(){
        this.activated = false;
        this.resetDate = null;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof UserEntity)){
            return false;
        }

        return id != null && id.equals(((UserEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
}
