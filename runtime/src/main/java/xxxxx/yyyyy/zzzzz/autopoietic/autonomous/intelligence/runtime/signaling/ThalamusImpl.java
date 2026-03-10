package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Autopoiesis autopoiesis;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;

    @Inject
    public ThalamusImpl(Cortex cortex,
                        Knowledge knowledge, Episode episode,
                        Autopoiesis autopoiesis,
                        Transmitter transmitter, Nucleus nucleus,
                        Repository<Area> areaRepository) {
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.autopoiesis = autopoiesis;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
    }

    @Override
    public void relay(Impulse impulse) {
        var projection = this.transmitter.transmit(impulse, Projection.class);
        this.nucleus.integrate(projection, () -> {
            var area = this.areaRepository.find(projection.area());
            if (area == null) {
                throw new IllegalStateException(
                    "Area not found: " + projection.area());
            }
            this.cortex.respond(
                new ImpulseImpl(impulse.signal(), area));
        });
    }

    @Override
    public void burst() {
        var spindle = this.transmitter.transmit(null, Spindle.class);
        this.nucleus.integrate(spindle, () -> {
            this.autopoiesis.conserve();
            this.knowledge.promote();
            this.episode.decay();
            this.knowledge.decay();
        });
    }
}
