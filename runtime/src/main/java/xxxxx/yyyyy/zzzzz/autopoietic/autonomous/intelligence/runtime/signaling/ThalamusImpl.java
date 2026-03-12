package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Autopoiesis autopoiesis;
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Transmitter transmitter;
    private final Nucleus nucleus;

    @Inject
    public ThalamusImpl(Autopoiesis autopoiesis, Cortex cortex,
                        Knowledge knowledge, Episode episode,
                        Transmitter transmitter, Nucleus nucleus) {
        this.autopoiesis = autopoiesis;
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
    }

    @Override
    public void relay(Impulse impulse) {
        var projection = this.transmitter.transmit(impulse, Projection.class);
        this.nucleus.integrate(projection, () -> {
            this.cortex.respond(
                new ImpulseImpl(
                    impulse.signal(),
                    ((ImpulseImpl) impulse).mode(),
                    projection.area()));
        });
    }

    @Override
    public void burst() {
        var spindle = this.transmitter.transmit(null, Spindle.class);
        this.nucleus.integrate(spindle, () -> {
            this.autopoiesis.conserve();
            this.knowledge.promote();
            allOf(
                runAsync(this.episode::decay),
                runAsync(this.knowledge::decay)
            ).join();
        });
    }
}
