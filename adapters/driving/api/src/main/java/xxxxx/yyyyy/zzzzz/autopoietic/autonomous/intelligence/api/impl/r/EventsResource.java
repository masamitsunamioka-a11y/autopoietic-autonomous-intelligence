package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.r;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.SubscriberImpl;

@Path("/events")
@RequestScoped
public class EventsResource {
    private static final Logger logger = LoggerFactory.getLogger(EventsResource.class);
    private final Publisher publisher;

    public EventsResource() {
        this.publisher = null;
    }

    @Inject
    public EventsResource(Publisher publisher) {
        this.publisher = publisher;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void events(@Context SseEventSink sink, @Context Sse sse) {
        this.publisher.subscribe(new SubscriberImpl(sink, sse));
    }
}
