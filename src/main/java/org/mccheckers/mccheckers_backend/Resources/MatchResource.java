package org.mccheckers.mccheckers_backend.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.mccheckers.mccheckers_backend.dto.MatchRequestDTO;
import org.mccheckers.mccheckers_backend.dto.MatchResponseDTO;
import org.mccheckers.mccheckers_backend.service.MatchService;
import org.mccheckers.mccheckers_backend.service.UserService;

import java.util.Collections;

@Path("/matches")
public class MatchResource {

    @Inject
    private MatchService matchService;
    @Inject
    private UserService userService;

    @GET
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMatches() {
        return Response.ok(matchService.getAllMatches()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatch(@PathParam("id") int id) {
        try {
            MatchResponseDTO dto = matchService.getMatchById(id);
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/create")
    @RolesAllowed("MODERATOR")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMatch(MatchRequestDTO dto) {
        try {
            MatchResponseDTO matchResponseDTO = matchService.createMatch(dto);
            return Response.status(Response.Status.CREATED).entity(matchResponseDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchesUser(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return Response.ok(matchService.getMatchesUser(userService.getIdByUsername(username))).build();
    }
}
