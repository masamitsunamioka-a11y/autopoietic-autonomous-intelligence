package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Repository<Area, Engravable> areaRepository;
    private final Encoder encoder;
    private final Nucleus nucleus;

    @Inject
    public ThalamusImpl(Repository<Area, Engravable> areaRepository,
                        Encoder encoder, Nucleus nucleus) {
        this.areaRepository = areaRepository;
        this.encoder = encoder;
        this.nucleus = nucleus;
    }

    @Override
    public Impulse relay(Impulse impulse) {
        var output = this.integrate(impulse);
        var area = this.areaRepository.find(output.area());
        if (area == null) {
            throw new IllegalStateException();
        }
        return new ImpulseImpl(impulse.signal(), area);
    }

    private Projection integrate(Impulse impulse) {
        var signal = this.encoder.encode(impulse, Thalamus.class);
        return this.nucleus.integrate(new ImpulseImpl(signal, impulse.area()), Projection.class);
    }
}
