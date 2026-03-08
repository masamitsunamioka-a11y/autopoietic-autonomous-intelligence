package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.integrative;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Decoder;

@ApplicationScoped
public class DecoderImpl implements Decoder {
    private static final Logger logger = LoggerFactory.getLogger(DecoderImpl.class);
    private final Service<Input, Object> decodicService;

    public record Input(
        String signal,
        Class<?> response) {
    }

    @Inject
    public DecoderImpl(@Decodic Service<Input, Object> decodicService) {
        this.decodicService = decodicService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T decode(String signal, Class<T> response) {
        return (T) this.decodicService.call(
            new Input(signal, response));
    }
}
