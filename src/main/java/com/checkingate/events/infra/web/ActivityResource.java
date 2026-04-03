package com.checkingate.events.infra.web;

import com.checkingate.events.application.usecase.CheckInActivityUseCase;
import com.checkingate.events.application.usecase.ListUserCheckInsUseCase;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/activities")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    CheckInActivityUseCase checkInActivityUseCase;

    @Inject
    ListUserCheckInsUseCase listUserCheckInsUseCase;

    @POST
    @Path("/{activity_id}/checkin")
    public Response checkIn(@PathParam("activity_id") String activityId, CheckInRequest request) {
        String userId = jwt.getClaim("user_id");
        var checkIn = checkInActivityUseCase.execute(new CheckInActivityUseCase.Input(
            userId,
            activityId,
            request != null ? request.latitude : null,
            request != null ? request.longitude : null
        ));
        return Response.status(201).entity(checkIn).build();
    }

    @GET
    @Path("/checkins")
    public Response listUserCheckIns() {
        String userId = jwt.getClaim("user_id");
        var checkIns = listUserCheckInsUseCase.execute(userId);
        return Response.ok(checkIns).build();
    }

    public record CheckInRequest(Double latitude, Double longitude) {}
}
