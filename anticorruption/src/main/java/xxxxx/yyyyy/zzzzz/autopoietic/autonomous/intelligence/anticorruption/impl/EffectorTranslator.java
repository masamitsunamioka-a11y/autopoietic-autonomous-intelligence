package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

@ApplicationScoped
public class EffectorTranslator implements Translator<Effector, String> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorTranslator.class);
    private final String effectorPackage;

    public EffectorTranslator() {
        var configuration = new Configuration("anticorruption.yaml");
        this.effectorPackage = configuration.get("anticorruption.effectors.package");
    }

    @Override
    public Effector translateFrom(String id, String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String translateTo(String id, Effector effector) {
        return """
            package %s;
            import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
            import java.util.Map;
            public class %s implements Effector {
                @Override public String name() { return "%s"; }
                @Override public String function() { return "%s"; }
                @Override
                public Map<String, Object> fire(Map<String, Object> input) {
                    return Map.of(
                            "message", "Effector [%s] successfully executed."
                    );
                }
            }
            """
            .formatted(this.effectorPackage, id, id, effector.function(), id);
    }
}
