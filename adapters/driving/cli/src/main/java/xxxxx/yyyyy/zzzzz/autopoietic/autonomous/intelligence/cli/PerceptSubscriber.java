package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

import java.util.Set;

@ApplicationScoped
public class PerceptSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(PerceptSubscriber.class);
    private static final Set<String> VOCALIZE = Set.of("VOCALIZE", "INHIBIT", "FIRE");

    public void onPercept(@Observes Percept percept) {
        if (VOCALIZE.contains(percept.location().toUpperCase())) {
            System.out.printf("%n%s>%n%s%n",
                percept.location(), percept.content());
        } else {
            System.out.printf("%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n",
                percept.location(), percept.content());
        }
    }
}
