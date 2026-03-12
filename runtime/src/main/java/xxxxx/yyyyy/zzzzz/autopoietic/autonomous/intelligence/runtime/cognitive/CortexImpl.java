package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import java.time.Instant;
import java.util.Map;

import static java.time.Duration.between;
import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl.*;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Autopoiesis autopoiesis;
    private final Event<Percept> event;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final Repository<Effector> effectorRepository;
    private final HabituationGuard habituationGuard;

    @Inject
    public CortexImpl(Autopoiesis autopoiesis, Event<Percept> event,
                      Knowledge knowledge, Episode episode,
                      Transmitter transmitter, Nucleus nucleus,
                      Repository<Area> areaRepository,
                      Repository<Effector> effectorRepository) {
        this.autopoiesis = autopoiesis;
        this.event = event;
        this.knowledge = knowledge;
        this.episode = episode;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.effectorRepository = effectorRepository;
        this.habituationGuard = new HabituationGuard();
    }

    @Override
    public void respond(Impulse impulse) {
        var mode = ((ImpulseImpl) impulse).mode();
        if (mode != null) {
            var trace = switch (mode) {
                case CEN -> new TraceImpl("user", impulse.signal());
                case DMN -> introspected(
                    String.valueOf(impulse.signal()));
            };
            this.episode.encode(trace);
        }
        var start = Instant.now();
        var decision = this.transmitter.transmit(impulse, Decision.class);
        this.nucleus.integrate(decision, () -> {
            switch (decision.process()) {
                case "POTENTIATE" -> this.potentiate(impulse);
                case "PROJECT" -> this.project(decision, impulse);
                default -> this.execute(decision, impulse, start);
            }
        });
    }

    private void potentiate(Impulse impulse) {
        this.autopoiesis.compensate(impulse);
        this.respond(impulse);
    }

    private void project(Decision decision, Impulse impulse) {
        var area = this.areaRepository.find(decision.area());
        if (area == null) {
            this.episode.encode(unresolvedArea(decision.area()));
            this.respond(impulse);
        } else {
            this.respond(new ImpulseImpl(
                impulse.signal(), null, area.id()));
        }
    }

    private void execute(Decision decision, Impulse impulse, Instant start) {
        /// @formatter:off
        var effectorName = switch (decision.process()) {
            case "VOCALIZE" -> "VocalizeEffector";
            case "INHIBIT"  -> "InhibitEffector";
            default         -> decision.effector();
        };
        /// @formatter:on
        if (this.habituationGuard.observe(effectorName)) {
            this.episode.encode(habituatedEffector(effectorName));
            this.respond(impulse);
            return;
        }
        var effector = this.effectorRepository.find(effectorName);
        if (effector == null) {
            this.episode.encode(unresolvedEffector(effectorName));
            this.respond(impulse);
            return;
        }
        var input = this.input(decision);
        var output = effector.fire(input);
        var content = String.valueOf(
            output.getOrDefault("content", output));
        switch (decision.process()) {
            case "VOCALIZE" -> {
                this.episode.encode(
                    vocalized(impulse.direction(), content));
                this.perceive(content, impulse, decision, start);
            }
            case "INHIBIT" -> {
                this.episode.encode(inhibited(content));
                this.perceive(content, impulse, decision, start);
            }
            default -> {
                this.episode.encode(
                    fired(effectorName, content));
                this.respond(impulse);
            }
        }
    }

    private Map<String, Object> input(Decision decision) {
        return switch (decision.process()) {
            case "VOCALIZE", "INHIBIT" -> Map.of("response", (Object) decision.response());
            default -> this.knowledge.retrieve();
        };
    }

    private void perceive(
        String content, Impulse impulse,
        Decision decision, Instant start) {
        var duration = between(start, Instant.now()).toMillis();
        this.event.fire(new PerceptImpl(
            content,
            impulse.direction(),
            decision.confidence(),
            duration));
    }
}
