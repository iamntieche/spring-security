package com.mfoumgroup.authentification.auth.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtil {
    private static final int DEF_COUNT = 20;
    private static final int DEF_PASS = 10;
    static SecureRandom random = new SecureRandom();
    private RandomUtil() {
    }

    /**
     * Generate a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return generateSecureString(DEF_PASS);
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
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
