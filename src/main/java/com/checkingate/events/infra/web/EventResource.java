package com.checkingate.events.infra.web;

import java.time.Instant;
import java.util.List;

import com.checkingate.events.application.usecase.CreateActivitiesUseCase;
import com.checkingate.events.application.usecase.CreateEventUseCase;
import com.checkingate.events.application.usecase.FinishEventUseCase;
import com.checkingate.events.application.usecase.GetEventDetailsUseCase;
import com.checkingate.events.application.usecase.GetEventWithActivitiesUseCase;

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

@Path("/events")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    CreateEventUseCase createEventUseCase;

    @Inject
    CreateActivitiesUseCase createActivitiesUseCase;

    @Inject
    GetEventWithActivitiesUseCase getEventWithActivitiesUseCase;

    @Inject
    GetEventDetailsUseCase getEventDetailsUseCase;

    @Inject
    FinishEventUseCase finishEventUseCase;

    @POST
    public Response createEvent(CreateEventRequest request) {
        String userId = jwt.getClaim("user_id");
        var event = createEventUseCase.execute(new CreateEventUseCase.Input(
            userId,
            request.name,
            request.allowedDomains,
            request.description,
            request.startDate,
            request.endDate
        ));
        return Response.status(201).entity(event).build();
    }

    @POST
    @Path("/activities")
    public Response createActivities(CreateActivitiesRequest request) {
        String userId = jwt.getClaim("user_id");
        var activities = createActivitiesUseCase.execute(new CreateActivitiesUseCase.Input(
            userId,
            request.eventId,
            request.activities.stream()
                .map(a -> new CreateActivitiesUseCase.ActivityInput(
                    a.name, a.description, a.startDate, a.endDate,
                    a.latitude, a.longitude, a.maxDistance
                ))
                .toList()
        ));
        return Response.status(201).entity(activities).build();
    }

    @GET
    @Path("/{event_id}/activities")
    public Response getEventWithActivities(@PathParam("event_id") String eventId) {
        var output = getEventWithActivitiesUseCase.execute(eventId);
        return Response.ok(output).build();
    }

    @GET
    @Path("/{event_id}/details")
    public Response getEventDetails(@PathParam("event_id") String eventId) {
        var output = getEventDetailsUseCase.execute(eventId);
        return Response.ok(output).build();
    }

    @POST
    @Path("/{event_id}/finish")
    public Response finishEvent(@PathParam("event_id") String eventId) {
        String userId = jwt.getClaim("user_id");
        finishEventUseCase.execute(new FinishEventUseCase.Input(eventId, userId));
        return Response.ok(new MessageResponse("event finished successfully")).build();
    }

    // Request/Response DTOs
    public record CreateEventRequest(
        String name,
        List<String> allowedDomains,
        String description,
        Instant startDate,
        Instant endDate
    ) {}

    public record CreateActivitiesRequest(
        String eventId,
        List<ActivityRequest> activities
    ) {}

    public record ActivityRequest(
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        Double latitude,
        Double longitude,
        Double maxDistance
    ) {}

    record MessageResponse(String message) {}
}
