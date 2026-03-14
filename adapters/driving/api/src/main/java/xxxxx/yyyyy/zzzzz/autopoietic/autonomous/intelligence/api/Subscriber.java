package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@ApplicationScoped
public class Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
    private final Events events;

    @Inject
    public Subscriber(Events events) {
        this.events = events;
    }

    public void onStimulus(@Observes Stimulus stimulus) {
        this.events.queue(new Event(
            "user",
            "user",
            stimulus.energy().toString()));
    }

    public void onPercept(@Observes Percept percept) {
        this.events.queue(new Event(
            "message",
            percept.location(),
            percept.content()));
    }
}
