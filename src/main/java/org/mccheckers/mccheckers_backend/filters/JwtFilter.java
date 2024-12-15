package org.mccheckers.mccheckers_backend.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.mccheckers.mccheckers_backend.Config;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Provider
@Priority(1)
public class JwtFilter implements ContainerRequestFilter {

    private static final String secretString = Config.getJwtSecret();
    SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("/auth/login")) {
            return; // Skip JWT validation for login
        }

        String authorizationHeader = requestContext.getHeaderString("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

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
