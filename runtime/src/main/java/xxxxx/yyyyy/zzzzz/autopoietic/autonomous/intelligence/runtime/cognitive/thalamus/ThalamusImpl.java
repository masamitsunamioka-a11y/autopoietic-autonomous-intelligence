package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.thalamus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Repository<Area, Engram> areaRepository;

    @Inject
    public ThalamusImpl(Nucleus nucleus, Encoder encoder,
                        Repository<Area, Engram> areaRepository) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.areaRepository = areaRepository;
    }

    @Override
    public Impulse relay(Impulse impulse) {
        var signal = this.encoder.encode(impulse, Thalamus.class);
        var output = this.nucleus.integrate(signal, Projection.class);
        var area = this.areaRepository.find(output.area());
        if (area == null) {
            throw new IllegalStateException();
        }
        return new ImpulseImpl(impulse.signal(), area);
    }
}
