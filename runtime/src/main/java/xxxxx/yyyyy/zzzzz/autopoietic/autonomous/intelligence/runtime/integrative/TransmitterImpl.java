package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.integrative;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning.Potentiation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning.Pruning;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.modulatory.Fluctuation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.Projection;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Decoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.learning.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

import java.util.Map;

@ApplicationScoped
public class TransmitterImpl implements Transmitter {
    private static final Logger logger = LoggerFactory.getLogger(TransmitterImpl.class);
    /// @formatter:off
    private static final Map<Class<?>, Class<?>> PATHWAYS = Map.of(
        Decision.class,     Cortex.class,
        Projection.class,   Thalamus.class,
        Fluctuation.class,  Default.class,
        Potentiation.class, Plasticity.class,
        Pruning.class,      Plasticity.class);
    /// @formatter:on
    private final Encoder encoder;
    private final Decoder decoder;
    private final Service<String, String> diffusicService;

    @Inject
    public TransmitterImpl(Encoder encoder, Decoder decoder,
                           @Diffusic Service<String, String> diffusicService) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.diffusicService = diffusicService;
    }

    @Override
    public <T> T transmit(Impulse impulse, Class<T> response) {
        var caller = PATHWAYS.get(response);
        var signal = this.encoder.encode(impulse, caller);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", signal);
        }
        var raw = this.diffusicService.call(signal);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", raw);
        }
        return this.decoder.decode(raw, response);
    }
}
