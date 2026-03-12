package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Compensation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Conservation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Promotion;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.modulatory.Fluctuation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.Projection;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.Spindle;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Decoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import java.util.Map;

@ApplicationScoped
public class TransmitterImpl implements Transmitter {
    private static final Logger logger = LoggerFactory.getLogger(TransmitterImpl.class);
    /// @formatter:off
    private static final Map<Class<?>, Class<?>> PATHWAYS = Map.of(
        Decision.class,     Cortex.class,
        Projection.class,   Thalamus.class,
        Fluctuation.class,  Default.class,
        Compensation.class, Autopoiesis.class,
        Conservation.class, Autopoiesis.class,
        Promotion.class,    Knowledge.class);
    /// @formatter:on
    public record Input(String prompt, Class<?> caller) {
    }

    private final Encoder encoder;
    private final Decoder decoder;
    private final Service<Input, String> diffusicService;

    @Inject
    public TransmitterImpl(Encoder encoder, Decoder decoder,
                           @Diffusic Service<Input, String> diffusicService) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.diffusicService = diffusicService;
    }

    @Override
    public <T> T transmit(Impulse impulse, Class<T> response) {
        var caller = PATHWAYS.get(response);
        if (caller == null) {
            return response.cast(new Spindle());
        }
        var signal = this.encoder.encode(impulse, caller);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", signal);
        }
        var raw = this.diffusicService.call(new Input(signal, caller));
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", raw);
        }
        return this.decoder.decode(raw, response);
    }
}
