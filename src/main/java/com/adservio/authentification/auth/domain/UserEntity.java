package com.adservio.authentification.auth.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity(name = "User")
@Table(name = "user")
public class UserEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean activated = false;
    private String langKey;
    private String imageUrl;
    private String activationKey;
    private String resetKey;
    private Instant resetDate = null;

    @ManyToMany
     private Set<AuthorityEntity> authorities = new HashSet<>();

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
