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
    private final SseRegistry sseRegistry;

    @Inject
    public Subscriber(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    public void onStimulus(@Observes Stimulus stimulus) {
        this.sseRegistry.broadcast(
            this.sseRegistry.buildJson(
                "user", "user", stimulus.signal().toString()));
    }

    public void onPercept(@Observes Percept percept) {
        this.sseRegistry.broadcast(
            this.sseRegistry.buildJson(
                "message", percept.location(), percept.content()));
    }
}
