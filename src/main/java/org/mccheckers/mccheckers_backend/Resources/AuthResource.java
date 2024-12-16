package org.mccheckers.mccheckers_backend.Resources;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.db.UserDAO;
import org.mccheckers.mccheckers_backend.service.AdminService;
import org.mccheckers.mccheckers_backend.service.UserService;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Path("/auth")
public class AuthResource {

    //TODO logout

    @Inject
    private UserService userService;
    @Inject
    private AdminService adminService;

    private static final String secretString = Config.getJwtSecret();
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    private static final long EXPIRATION_TIME = Long.parseLong(Config.getJwtExpirationTime());

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if ("admin".equals(username) && "admin".equals(password)) {
            String token = Jwts.builder()
                    .subject(user.getUsername())
                    .claim("roles", Arrays.asList("ADMIN", "USER", "MODERATOR")) //todo verify the necessity of MOD role
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY)
                    .compact();

            return Response.ok(new TokenResponse(token)).build();
        }
        if (!userService.comparePasswords(username, password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized access")
                    .build();
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

        return Response.ok(new TokenResponse(token)).build();

    }

    public static class User {
        private String username;
        private String password;

        public User() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}

