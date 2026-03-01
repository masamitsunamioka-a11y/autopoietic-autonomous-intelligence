package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class NucleusImpl implements Nucleus {
    private static final Logger logger = LoggerFactory.getLogger(NucleusImpl.class);
    private final Serializer serializer;
    private final Service<String, String> service;
    private final Validator validator;

    @Inject
    public NucleusImpl(Serializer serializer, Service<String, String> service) {
        this.serializer = serializer;
        this.service = service;
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T integrate(Impulse impulse, Class<T> type) {
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", impulse.signal());
        }
        var text = this.service.call(impulse.signal());
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", text);
        }
        T result = this.serializer.deserialize(text, type);
        this.validate(result);
        return result;
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
