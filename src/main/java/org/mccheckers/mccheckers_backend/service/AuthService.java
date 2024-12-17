package org.mccheckers.mccheckers_backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.Resources.AuthResource;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequestScoped
public class AuthService {
    private static final String secretString = Config.getJwtSecret();
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    private static final long EXPIRATION_TIME = Long.parseLong(Config.getJwtExpirationTime());

    @Inject
    private UserService userService;
    @Inject AdminService adminService;

    public String login(String username, String password) {
        if (username.equals(Config.getAdminUsername()) && password.equals(Config.getAdminPassword())) {
            String token = Jwts.builder()
                    .subject(username)
                    .claim("roles", Arrays.asList("ADMIN", "USER", "MODERATOR")) //todo verify the necessity of MOD role
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY)
                    .compact();

            return token;
        }
        if (!userService.comparePasswords(username, password)) {
            return null;
        }

        List<String> roles = new ArrayList<>();
        roles.add("USER");
        if (adminService.isModerator(userService.getIdByUsername(username))) {
            roles.add("MODERATOR");
        }

        String token = Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();

        return token;
    }
}
