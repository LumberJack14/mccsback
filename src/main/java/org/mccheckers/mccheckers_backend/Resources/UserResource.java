package org.mccheckers.mccheckers_backend.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.mccheckers.mccheckers_backend.dto.UserRequestDTO;
import org.mccheckers.mccheckers_backend.dto.UserResponseDTO;
import org.mccheckers.mccheckers_backend.service.UserService;

import java.util.Collections;

@Path("/users")
public class UserResource {

    @Inject
    private UserService userService;

    // Endpoint to create a new user
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response createUser(UserRequestDTO userDTO) {
        try {
            int id = userService.registerUser(userDTO);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            UserResponseDTO dto = userService.getUserById(userService.getIdByUsername(username));
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") int id) {
        UserResponseDTO user = userService.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", " User with ID " + id + " not found."))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response updateUser(@PathParam("id") int id, UserRequestDTO userDTO, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();

        if (securityContext.isUserInRole("ADMIN") || username.equals(userDTO.getUsername())) {
            UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
            if (updatedUser != null) {
                return Response.ok(updatedUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Collections.singletonMap("error", "User with ID " + id + " not found."))
                        .build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Collections.singletonMap("error", "You are not authorized to update this user."))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteUser(@PathParam("id") int id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", " User with ID " + id + " not found."))
                    .build();
        }
    }
}
