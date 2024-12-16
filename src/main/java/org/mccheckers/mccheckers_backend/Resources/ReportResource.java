package org.mccheckers.mccheckers_backend.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.mccheckers.mccheckers_backend.dto.ReportRequestDTO;
import org.mccheckers.mccheckers_backend.model.Report;
import org.mccheckers.mccheckers_backend.service.ReportService;
import org.mccheckers.mccheckers_backend.service.UserService;

import java.util.Collections;
import java.util.List;

@Path("/reports")
public class ReportResource {
    @Inject
    private ReportService reportService;
    @Inject
    private UserService userService;

    @POST
    @Path("/create")
    @RolesAllowed("MODERATOR")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReport(ReportRequestDTO dto) {
        try {
            Report report = reportService.createReport(dto);
            return Response.status(Response.Status.CREATED).entity(report).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportsUser(@PathParam("id") int userId, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        if (securityContext.isUserInRole("ADMIN") || username.equals(userService.getUserById(userId).getUsername())) {
            return Response.ok(reportService.getReportsUser(userId)).build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity(Collections.singletonMap("error", "You are not authorized to see this user's reports."))
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportById(@PathParam("id") int id, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        int userId = userService.getIdByUsername(username);
        Report report = reportService.getReportById(id);
        if (
                securityContext.isUserInRole("ADMIN") ||
                        userId == report.getUserId() ||
                        userId == report.getModeratorId()
        ) {
            return Response.ok(report).build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity(Collections.singletonMap("error", "You are not authorized to see this report."))
                .build();
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "MODERATOR"})
    public Response getMyReports(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return Response.ok(reportService.getReportsUser(userService.getIdByUsername(username))).build();
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAllReports() {
        return Response.ok(reportService.getAllReports()).build();
    }
}
