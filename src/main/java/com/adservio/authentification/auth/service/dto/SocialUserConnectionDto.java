package com.adservio.authentification.auth.service.dto;

import com.adservio.authentification.auth.domain.SocialUserConnectionEntity;

import lombok.Data;

@Data
public class SocialUserConnectionDto {
    private Long id;
    private String userId;
    private String providerId;
    private String providerUserId;
    private Long rank;
    private String displayName;
    private String profileURL;
    private String imageURL;
    private String accessToken;
    private String secret;
    private String refreshToken;
    private Long expireTime;
    private String email;

    public SocialUserConnectionDto(){}
    public SocialUserConnectionDto(SocialUserConnectionEntity socialUserConnection){
        this.id = socialUserConnection.getId();
        this.userId = socialUserConnection.getUserId();
        this.providerId = socialUserConnection.getProviderId();
        this.providerUserId = socialUserConnection.getProviderUserId();
        this.rank = socialUserConnection.getRank();
        this.displayName = socialUserConnection.getDisplayName();
        this.profileURL = socialUserConnection.getProfileURL();
        this.imageURL = socialUserConnection.getImageURL();
        this.accessToken = socialUserConnection.getAccessToken();
        this.secret = socialUserConnection.getSecret();
        this.refreshToken = socialUserConnection.getRefreshToken();
        this.expireTime = socialUserConnection.getExpireTime();
        this.email = socialUserConnection.getEmail();
    }
}
