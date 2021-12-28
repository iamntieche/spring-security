package com.mfoumgroup.authentification.auth.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity(name = "SocialUserConnection")
@Table(name = "socialUserConnection")
@ToString
public class SocialUserConnectionEntity extends  AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @NotNull
    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @NotNull
    @Column(nullable = false)
    private Long rank;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "profile_url")
    private String profileURL;

    @Column(name = "image_url")
    private String imageURL;
    @Email
    private  String email;

    @NotNull
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "secret")
    private String secret;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expire_time")
    private Long expireTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        return id != null && id.equals(((SocialUserConnectionEntity)o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
