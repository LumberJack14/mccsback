package org.mccheckers.mccheckers_backend.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mccheckers.mccheckers_backend.dto.BlockRequestDTO;
import org.mccheckers.mccheckers_backend.dto.ModeratorRequestDTO;
import org.mccheckers.mccheckers_backend.service.AdminService;

import java.util.Collections;

@Path("/admin")
public class AdminResource {

    @Inject
    private AdminService adminService;

    @GET
    @RolesAllowed("ADMIN")
    public Response getAdminData() {
        return Response.ok("Welcome, Admin!").build();
    }

    @POST
    @Path("/moderator")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response addModerator(ModeratorRequestDTO dto) {
        try {
            int id = adminService.addModerator(dto.getUserId());
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/moderator/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response removeModerator(@PathParam("id") int userId) {
        try {
            adminService.removeModerator(userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/activate/{id}")
    @RolesAllowed("ADMIN")
    public Response activateUser(@PathParam("id") int userId) {
        try {
            adminService.activateUser(userId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/deactivate/{id}")
    @RolesAllowed("ADMIN")
    public Response deactivateUser(@PathParam("id") int userId) {
        try {
            adminService.deactivateUser(userId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/block")
    @RolesAllowed("ADMIN")
    public Response blockUser(BlockRequestDTO dto) {
        try {
            adminService.blockUser(dto);
            return Response.ok().build();
        } catch(Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }
}
