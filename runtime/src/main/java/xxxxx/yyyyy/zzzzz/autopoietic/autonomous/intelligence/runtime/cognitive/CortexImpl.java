package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.Map;

/// In the future, scope to per-session
@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private static final Map<String, String> EFFECTORS = Map.of(
        "VOCALIZE", "VocalizeEffector",
        "INHIBIT", "InhibitEffector");
    private final Autopoiesis autopoiesis;
    private final Event<Potential> potentialEvent;
    private final Event<Percept> perceptEvent;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final Repository<Effector> effectorRepository;
    private final Service<Impulse, Potential> transmitter;
    private final Habituation habituation;

    @Inject
    public CortexImpl(Autopoiesis autopoiesis,
                      Event<Potential> potentialEvent, Event<Percept> perceptEvent,
                      Knowledge knowledge, Episode episode, Nucleus nucleus,
                      Repository<Area> areaRepository,
                      Repository<Effector> effectorRepository,
                      @Releasic @Diffusic @Bindic
                      Service<Impulse, Potential> transmitter) {
        this.autopoiesis = autopoiesis;
        this.potentialEvent = potentialEvent;
        this.perceptEvent = perceptEvent;
        this.knowledge = knowledge;
        this.episode = episode;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.effectorRepository = effectorRepository;
        this.transmitter = transmitter;
        this.habituation = new Habituation();
    }

    @Override
    public void respond(Impulse impulse) {
        this.respond(impulse, true);
    }

    private void respond(Impulse impulse, boolean initial) {
        if (initial) {
            this.encode(impulse.afferent(), impulse.signal());
        }
        var decision = (Decision) this.transmitter.call(new ImpulseImpl(impulse.signal(), this.label(), impulse.efferent()));
        this.nucleus.integrate(decision, x -> {
            this.potentialEvent.fire(x);
            this.process(x, impulse);
        });
    }

    private void process(Decision decision, Impulse impulse) {
        switch (decision.process()) {
            case "POTENTIATE" -> {
                this.autopoiesis.compensate(impulse);
                this.respond(impulse, false);
            }
            case "PROJECT" -> {
                var area = this.areaRepository.find(decision.area());
                if (area != null) {
                    this.respond(new ImpulseImpl(impulse.signal(), impulse.afferent(), area.id()), false);
                } else {
                    this.encode("[SYSTEM]", "[WARNING] Area '" + decision.area() + "' not found.");
                    this.respond(impulse, false);
                }
            }
            case "VOCALIZE", "INHIBIT" -> {
                if (this.habituation.habituated(decision)) {
                    return;
                }
                var output = this.effect(this.resolve(decision), Map.of("response", decision.response()));
                this.encode(impulse.efferent(), output.get("content"));
                this.perceptEvent.fire(new PerceptImpl(String.valueOf(output.get("content")), impulse.efferent(), decision.amplitude(), 1));
            }
            default -> {
                if (this.habituation.habituated(decision)) {
                    return;
                }
                var output = this.effect(this.resolve(decision), Map.of("knowledge", this.knowledge.retrieve()));
                this.encode(impulse.efferent(), output.get("content"));
                this.respond(impulse, false);
            }
        }
    }

    private String resolve(Decision decision) {
        return EFFECTORS.getOrDefault(decision.process(), decision.effector());
    }

    private Map<String, Object> effect(String id, Map<String, Object> input) {
        return this.effectorRepository.find(id).fire(input);
    }

    private void encode(String id, Object content) {
        this.episode.encode(new TraceImpl(id, content));
    }

    private String label() {
        return Cortex.class.getSimpleName();
    }
}
