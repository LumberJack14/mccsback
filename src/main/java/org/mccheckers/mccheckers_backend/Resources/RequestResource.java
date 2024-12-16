package org.mccheckers.mccheckers_backend.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.mccheckers.mccheckers_backend.dto.RequestRequestDTO;
import org.mccheckers.mccheckers_backend.model.Request;
import org.mccheckers.mccheckers_backend.service.RequestService;
import org.mccheckers.mccheckers_backend.service.UserService;

import java.util.Collections;

@Path("/requests")
public class RequestResource {

    @Inject
    private RequestService requestService;
    @Inject
    private UserService userService;

    @GET
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequests() {
        return Response.ok(requestService.getCurrentRequests()).build();
    }

    @POST
    @Path("/create")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRequests(RequestRequestDTO dto, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        Request request = requestService.createRequest(dto, userService.getIdByUsername(username));
        if (request == null) {
            return Response.status(Response.Status.CONFLICT).entity(Collections.singletonMap("error", "Server error while creating request"))
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(request.getId()).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"USER", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeToRequest(@PathParam("id") int requestId, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            requestService.subscribeToRequest(requestId, username);
            return Response.status(Response.Status.CREATED).build();
        } catch(IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }

    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"USER", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribeFromRequest(@PathParam("id") int requestId, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            requestService.unsubscribeFromRequest(requestId, username);
            return Response.ok().build();
        } catch(IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequestsUser(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return Response.ok(requestService.getRequestsUser(userService.getIdByUsername(username))).build();
    }

}
