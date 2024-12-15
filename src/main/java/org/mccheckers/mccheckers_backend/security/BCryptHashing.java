package org.mccheckers.mccheckers_backend.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptHashing {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
