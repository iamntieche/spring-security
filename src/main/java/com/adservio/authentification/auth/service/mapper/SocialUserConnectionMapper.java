package com.adservio.authentification.auth.service.mapper;

import com.adservio.authentification.auth.domain.SocialUserConnectionEntity;
import com.adservio.authentification.auth.service.dto.SocialUserConnectionDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SocialUserConnectionMapper {
    public SocialUserConnectionEntity socialUserConnectionDtoToSocialConnection(SocialUserConnectionDto socialUserConnectionDto) {
        if(socialUserConnectionDto == null) return null;
        SocialUserConnectionEntity entity = new SocialUserConnectionEntity();
        entity.setId(socialUserConnectionDto.getId());
        entity.setRefreshToken(socialUserConnectionDto.getRefreshToken());
        entity.setImageURL(socialUserConnectionDto.getImageURL());
        entity.setUserId(socialUserConnectionDto.getUserId());
        entity.setProviderId(socialUserConnectionDto.getProviderId());
        entity.setProviderUserId(socialUserConnectionDto.getProviderUserId());
        entity.setExpireTime(socialUserConnectionDto.getExpireTime());
        entity.setDisplayName(socialUserConnectionDto.getDisplayName());
        entity.setAccessToken(socialUserConnectionDto.getAccessToken());
        entity.setSecret(socialUserConnectionDto.getSecret());
        entity.setRank(socialUserConnectionDto.getRank());
        entity.setProfileURL(socialUserConnectionDto.getProfileURL());
        return entity;
    }

    public List<SocialUserConnectionDto> socialUserConnectionsToSocialUsersConnectionDtos(List<SocialUserConnectionEntity> socialUserConnectionEntities) {
    return socialUserConnectionEntities.stream()
            .filter(Objects::nonNull)
                .map(this::socialUserConnectionToSocialUserConnectionDto).collect(Collectors.toList());
    }

    public SocialUserConnectionDto socialUserConnectionToSocialUserConnectionDto(SocialUserConnectionEntity socialUserConnectionEntity) {
        return new SocialUserConnectionDto(socialUserConnectionEntity);
    }

    public List<SocialUserConnectionEntity> socialConnectionDtosToSocialConnection(List<SocialUserConnectionDto> connectionDtos) {
        return  connectionDtos.stream().filter(Objects::nonNull).map(this::socialUserConnectionDtoToSocialConnection).collect(Collectors.toList());
    }
}
