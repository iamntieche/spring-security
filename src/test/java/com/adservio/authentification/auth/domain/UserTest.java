package com.adservio.authentification.auth.domain;


import static org.assertj.core.api.Assertions.assertThat;

import com.adservio.authentification.auth.util.TestUtil;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void equalsVerifier() throws Exception{
        TestUtil.equalsVerifier(UserEntity.class);
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        UserEntity user2 = new UserEntity();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }
    
}
