package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.r;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.SubscriberImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.PerceptGenerated;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.StimulusFired;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

@Path("/events")
@RequestScoped
public class EventsResource {
    private static final Logger logger = LoggerFactory.getLogger(EventsResource.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String RECEPTOR = "Receptor";
    private final Publisher publisher;
    private final Episode episode;

    public EventsResource() {
        this.publisher = null;
        this.episode = null;
    }

    @Inject
    public EventsResource(Publisher publisher, Episode episode) {
        this.publisher = publisher;
        this.episode = episode;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void events(
        @Context SseEventSink sink,
        @Context Sse sse,
        @HeaderParam("Last-Event-ID") String lastEventId) {
        this.replay(sink, sse, lastEventId);
        this.publisher.subscribe(new SubscriberImpl(sink, sse));
    }

    private void replay(SseEventSink sink, Sse sse, String lastEventId) {
        var traces = this.episode.retrieve();
        var skip = lastEventId != null;
        for (var trace : traces) {
            if (skip) {
                if (trace.id().equals(lastEventId)) {
                    skip = false;
                }
                continue;
            }
            sink.send(sse.newEventBuilder()
                .id(trace.id())
                .data(this.toJson(this.toEvent(trace)))
                .build());
        }
    }

    private Event toEvent(Trace trace) {
        var location = this.location(trace.id());
        if (RECEPTOR.equals(location)) {
            return new StimulusFired(String.valueOf(trace.content()));
        }
        return new PerceptGenerated(location, String.valueOf(trace.content()));
    }

    private String location(String id) {
        var index = id.indexOf("_");
        return index >= 0 ? id.substring(index + 1) : id;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
