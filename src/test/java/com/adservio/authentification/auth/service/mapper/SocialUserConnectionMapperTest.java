package com.adservio.authentification.auth.service.mapper;

import com.adservio.authentification.auth.domain.SocialUserConnectionEntity;
import com.adservio.authentification.auth.service.dto.SocialUserConnectionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SocialUserConnectionMapperTest {

    private SocialUserConnectionMapper socialUserConnectionMapper;
    private SocialUserConnectionDto socialUserConnectionDto;
    private SocialUserConnectionEntity socialUserConnectionEntity;

    private final String DEFAULT_ID = UUID.randomUUID().toString();
    private final  String DEFAULT_ACCESS_TOKEN = UUID.randomUUID().toString();
    private final String PROVIDER_USER_ID = UUID.randomUUID().toString();
    private final String PROVIDER_ID = UUID.randomUUID().toString();
    private final  String REFRESH_TOKEN = UUID.randomUUID().toString();

    @BeforeEach
    void init(){

        socialUserConnectionMapper = new SocialUserConnectionMapper();
        socialUserConnectionEntity = new SocialUserConnectionEntity();

        socialUserConnectionEntity.setUserId(DEFAULT_ID);
        socialUserConnectionEntity.setAccessToken(DEFAULT_ACCESS_TOKEN);
        socialUserConnectionEntity.setDisplayName("tiefa");
        socialUserConnectionEntity.setExpireTime(2L);
        socialUserConnectionEntity.setProviderUserId(PROVIDER_USER_ID);
        socialUserConnectionEntity.setProviderId(PROVIDER_ID);
        socialUserConnectionEntity.setImageURL("https://www.apollo-formation.com/wp-content/uploads/devops-2019.png");
        socialUserConnectionEntity.setRefreshToken(REFRESH_TOKEN);
        socialUserConnectionEntity.setCreatedBy("Youssouf");
        socialUserConnectionEntity.setCreatedDate(Instant.now());
        socialUserConnectionEntity.setLastModifiedBy("Youssouf");
        socialUserConnectionEntity.setLastModifiedDate(Instant.now().plus(5, ChronoUnit.MINUTES));

        socialUserConnectionDto = new SocialUserConnectionDto(socialUserConnectionEntity);
    }

    @Test
    void socialConnectionToSocialUserConnectionDtoShouldNotBeNull(){
        //when
        socialUserConnectionDto = socialUserConnectionMapper.socialUserConnectionToSocialUserConnectionDto(socialUserConnectionEntity);
        //then
        assertThat(socialUserConnectionDto).isNotNull();
        assertThat(socialUserConnectionDto.getUserId()).isEqualTo(socialUserConnectionEntity.getUserId());
    }

    @Test
    void socialUserConnectionDtoToSocialConnectionShouldNot(){
        //when
        socialUserConnectionEntity = socialUserConnectionMapper.socialUserConnectionDtoToSocialConnection(socialUserConnectionDto);
        //Then
        assertThat(socialUserConnectionEntity).isNotNull();
        assertThat(socialUserConnectionEntity.getAccessToken()).isEqualTo(socialUserConnectionDto.getAccessToken());
    }

    @Test
    void socialUsersConnectionsToSocialUserConnectionDtoOnlyNotNull(){
        //given
        List<SocialUserConnectionEntity> socialUserConnection = new ArrayList<>();
        socialUserConnection.add(socialUserConnectionEntity);
        socialUserConnection.add(null);
        //when
        List<SocialUserConnectionDto> socialUserConnectionDtos = socialUserConnectionMapper.socialUserConnectionsToSocialUsersConnectionDtos(socialUserConnection);
        assertThat(socialUserConnectionDtos).isNotNull();
        assertThat(socialUserConnectionDtos.size()).isEqualTo(1);

    }

    @Test
    void socialUsersConnectionsDtoToSocialUserConnectionOnlyNotNull(){
        List<SocialUserConnectionDto> connectionDtos = new ArrayList<>();
        connectionDtos.add(socialUserConnectionDto);
        connectionDtos.add(null);

        List<SocialUserConnectionEntity> connectionEntities = socialUserConnectionMapper.socialConnectionDtosToSocialConnection(connectionDtos);
        assertThat(connectionEntities).isNotNull();
        assertThat(connectionEntities.size()).isEqualTo(1);

    }

}
