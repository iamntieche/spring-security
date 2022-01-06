package com.mfoumgroup.authentification.auth.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtil {
    private static final int DEF_COUNT = 32;
    static SecureRandom random = new SecureRandom();
    private RandomUtil() {
    }

    /**
     * Generate a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return generateSecureString(6);
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key
     */
    public static String generateKey() {
        return generateSecureString(DEF_COUNT);
    }


    public static String generateSecureString(int length){
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
       // Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
