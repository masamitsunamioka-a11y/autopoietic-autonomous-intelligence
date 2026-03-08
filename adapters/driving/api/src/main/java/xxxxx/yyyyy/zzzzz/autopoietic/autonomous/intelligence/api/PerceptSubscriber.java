package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

import java.util.Set;

@ApplicationScoped
public class PerceptSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(PerceptSubscriber.class);
    private static final Set<String> VOCALIZE = Set.of("VOCALIZE", "INHIBIT", "FIRE");
    /// FIXME?
    private final SseRegistry sseRegistry;

    @Inject
    public PerceptSubscriber(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    public void onPercept(@Observes Percept percept) {
        var vocalize = VOCALIZE.contains(percept.location().toUpperCase());
        var type = vocalize
            ? "message"
            : "introspection";
        this.sseRegistry.broadcast(
            this.sseRegistry.buildJson(
                type, percept.location(), percept.content()));
    }
}
