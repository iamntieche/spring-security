package com.mfoumgroup.authentification.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndPasswordVM {
    private String key;

    private String newPassword;
}
