package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

@ApplicationScoped
public class PotentifierImpl implements Potentifier {
    private static final Logger logger = LoggerFactory.getLogger(PotentifierImpl.class);
    private final Serializer serializer;
    private final Validator validator;

    @Inject
    public PotentifierImpl(Serializer serializer, Validator validator) {
        this.serializer = serializer;
        this.validator = validator;
    }

    @Override
    public Potential potentify(String raw, Class<?> response) {
        var mapped = raw.replace(
            "\"confidence\"", "\"amplitude\"");
        var result = this.serializer.deserialize(mapped, response);
        this.validator.validate(result);
        return (Potential) result;
    }
}
