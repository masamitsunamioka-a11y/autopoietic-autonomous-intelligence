package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.integrative.DecoderImpl;

import static java.util.stream.Collectors.joining;

public class DecodingService implements Service<DecoderImpl.Input, Object> {
    private static final Logger logger = LoggerFactory.getLogger(DecodingService.class);
    private final Serializer serializer;
    private final Validator validator;

    public DecodingService(Serializer serializer) {
        this.serializer = serializer;
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object call(DecoderImpl.Input input) {
        try {
            var result = this.serializer.deserialize(
                input.signal(), input.response());
            this.validate(result);
            return result;
        } catch (Exception e) {
            logger.warn("root cause:\n{}", input.signal());
            throw e;
        }
    }

    private <T> void validate(T result) {
        var violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            var reasons = violations.stream()
                .map(x -> String.format("[%s] %s",
                    x.getPropertyPath(),
                    x.getMessage()))
                .collect(joining(", "));
            throw new IllegalStateException(reasons);
        }
    }
}
