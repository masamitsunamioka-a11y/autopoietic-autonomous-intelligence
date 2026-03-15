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
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final Repository<Effector> effectorRepository;
    private final Service<Impulse, Potential> transmitter;
    private final HabituationGuard habituationGuard;

    @Inject
    public CortexImpl(Autopoiesis autopoiesis, Event<Percept> event,
                      Knowledge knowledge, Episode episode, Nucleus nucleus,
                      Repository<Area> areaRepository,
                      Repository<Effector> effectorRepository,
                      @Releasic @Diffusic @Bindic
                      Service<Impulse, Potential> transmitter) {
        this.autopoiesis = autopoiesis;
        this.event = event;
        this.knowledge = knowledge;
        this.episode = episode;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.effectorRepository = effectorRepository;
        this.transmitter = transmitter;
        this.habituationGuard = new HabituationGuard();
    }

    /// fixme
    @Override
    public void respond(Impulse impulse) {
        var mode = ((ImpulseImpl) impulse).mode();
        if (mode != null) {
            var trace = switch (mode) {
                case CEN -> new TraceImpl("user", impulse.signal());
                case DMN -> introspected(impulse.signal());
            };
            this.episode.encode(trace);
        }
        var start = Instant.now();
        var decision = (Decision) this.transmitter.call(
            new ImpulseImpl(
                impulse.signal(), this.getClass(),
                impulse.efferent(), mode));
        this.nucleus.integrate(decision, x -> {
            switch (x.process()) {
                case "POTENTIATE" -> this.potentiate(impulse);
                case "PROJECT" -> this.project(x, impulse);
                default -> this.execute(x, impulse, start);
            }
        });
    }

    private void potentiate(Impulse impulse) {
        this.autopoiesis.compensate(impulse);
        this.respond(
            new ImpulseImpl(
                impulse.signal(), this.getClass(),
                impulse.efferent(), null));
    }

    private void project(Decision decision, Impulse impulse) {
        var area = this.areaRepository.find(decision.area());
        if (area == null) {
            this.episode.encode(unresolvedArea(decision.area()));
            this.respond(
                new ImpulseImpl(
                    impulse.signal(), this.getClass(),
                    impulse.efferent(), null));
        } else {
            this.respond(
                new ImpulseImpl(
                    impulse.signal(), this.getClass(),
                    area.id(), null));
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
            this.respond(
                new ImpulseImpl(
                    impulse.signal(), this.getClass(),
                    impulse.efferent(), null));
            return;
        }
        var effector = this.effectorRepository.find(effectorName);
        if (effector == null) {
            this.episode.encode(unresolvedEffector(effectorName));
            this.respond(
                new ImpulseImpl(
                    impulse.signal(), this.getClass(),
                    impulse.efferent(), null));
            return;
        }
        var input = this.input(decision);
        var output = effector.fire(input);
        var content = String.valueOf(output.getOrDefault("content", output));
        switch (decision.process()) {
            case "VOCALIZE" -> {
                this.episode.encode(vocalized(impulse.efferent(), content));
                this.perceive(content, impulse, decision, start);
            }
            case "INHIBIT" -> {
                this.episode.encode(inhibited(content));
                this.perceive(content, impulse, decision, start);
            }
            default -> {
                this.episode.encode(fired(effectorName, content));
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

    private void perceive(String content, Impulse impulse,
                          Decision decision, Instant start) {
        var duration = between(start, Instant.now()).toMillis();
        this.event.fire(
            new PerceptImpl(
                content,
                impulse.efferent(),
                decision.confidence(),
                duration));
    }
}
