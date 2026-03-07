package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Cortex cortex;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;

    @Inject
    public ThalamusImpl(Cortex cortex,
                        Transmitter transmitter, Nucleus nucleus,
                        Repository<Area> areaRepository) {
        this.cortex = cortex;
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
}
