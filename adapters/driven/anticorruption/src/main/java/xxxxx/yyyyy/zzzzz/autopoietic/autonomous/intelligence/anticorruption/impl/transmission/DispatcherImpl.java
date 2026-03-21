package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Compensation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Conservation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Consolidation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Fluctuation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Projection;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@ApplicationScoped
public class DispatcherImpl implements Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherImpl.class);
    private static final Map<String, Pathway> PATHWAYS = ofEntries(
        entry("Cortex", new Pathway(
            Cortex.class, Decision.class, "perception.md")),
        entry("Thalamus", new Pathway(
            Thalamus.class, Projection.class, "relay.md")),
        entry("Default", new Pathway(
            Default.class, Fluctuation.class, "default.md")),
        entry("Autopoiesis:compensate", new Pathway(
            Autopoiesis.class, Compensation.class, "compensation.md")),
        entry("Autopoiesis:conserve", new Pathway(
            Autopoiesis.class, Conservation.class, "conservation.md")),
        entry("Episode", new Pathway(
            Episode.class, Consolidation.class, "consolidation.md")));

    @Override
    public Pathway dispatch(Impulse impulse) {
        return PATHWAYS.get(this.pathwayKey(impulse));
    }

    private String pathwayKey(Impulse impulse) {
        return switch (impulse.afferent()) {
            case "Autopoiesis" -> impulse.signal() != null
                ? "Autopoiesis:compensate"
                : "Autopoiesis:conserve";
            default -> impulse.afferent();
        };
    }
}
