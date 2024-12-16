package org.mccheckers.mccheckers_backend.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.mccheckers.mccheckers_backend.Config;
import org.mccheckers.mccheckers_backend.Resources.AuthResource;
import org.mccheckers.mccheckers_backend.db.BlockDAO;
import org.mccheckers.mccheckers_backend.model.Block;
import org.mccheckers.mccheckers_backend.service.AdminService;
import org.mccheckers.mccheckers_backend.service.UserService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Provider
@Priority(1)
public class JwtFilter implements ContainerRequestFilter {

    private static final String secretString = Config.getJwtSecret();
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

    @Inject
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("/auth/login")) {
            return;
        }

        String authorizationHeader = requestContext.getHeaderString("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        if (AuthResource.isTokenBlacklisted(token)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token has been invalidated. Please log in again.").build());
        }

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

            int userId = userService.getIdByUsername(jws.getPayload().getSubject());
            List<Block> blocks = BlockDAO.getBlocksUser(userId);
            for (Block block: blocks) {
                if (block.getEndDate().after(new Date())) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("The user has been blocked with the reason: " + block.getCause()).build());
                }
            }

            SecurityContext securityContext = new SecurityContext() {
                @Context
                private HttpServletRequest request;
                @Override
                public Principal getUserPrincipal() {
                    return () -> jws.getPayload().getSubject();
                }

                @Override
                public boolean isUserInRole(String role) {
                    List<String> roles = (List<String>) jws.getPayload().get("roles");
                    return roles.contains(role);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getSecurityContext().isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            };

            requestContext.setSecurityContext(securityContext);


            //TODO add check for the expiration date

        } catch (JwtException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
