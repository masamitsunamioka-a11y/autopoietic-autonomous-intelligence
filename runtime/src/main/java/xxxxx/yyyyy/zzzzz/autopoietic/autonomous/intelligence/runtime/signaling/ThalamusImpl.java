package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Autopoiesis autopoiesis;
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Nucleus nucleus;
    private final Service<Impulse, Potential> transmitter;

    @Inject
    public ThalamusImpl(Autopoiesis autopoiesis, Cortex cortex,
                        Knowledge knowledge, Episode episode,
                        Nucleus nucleus,
                        @Releasic @Diffusic @Bindic
                        Service<Impulse, Potential> transmitter) {
        this.autopoiesis = autopoiesis;
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.nucleus = nucleus;
        this.transmitter = transmitter;
    }

    @Override
    public void relay(Impulse impulse) {
        var projection = (Projection) this.transmitter.call(
            new ImpulseImpl(
                impulse.signal(), this.getClass(),
                null, ((ImpulseImpl) impulse).mode()));
        this.nucleus.integrate(projection, x -> {
            this.cortex.respond(
                new ImpulseImpl(
                    impulse.signal(), this.getClass(),
                    x.area(), ((ImpulseImpl) impulse).mode()));
        });
    }

    @Override
    public void burst() {
        this.nucleus.integrate(new Spindle(), x -> {
            this.autopoiesis.conserve();
            this.knowledge.promote();
            allOf(
                runAsync(this.episode::decay),
                runAsync(this.knowledge::decay)
            ).join();
        });
    }
}
