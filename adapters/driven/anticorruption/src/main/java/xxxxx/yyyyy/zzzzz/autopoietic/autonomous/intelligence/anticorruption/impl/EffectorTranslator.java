package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

@ApplicationScoped
public class EffectorTranslator implements Translator<Effector, String> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorTranslator.class);
    private final ProxyProvider<Effector> proxyProvider;
    private final String effectorPackage;

    @Inject
    public EffectorTranslator(ProxyProvider<Effector> proxyProvider) {
        this.proxyProvider = proxyProvider;
        var configuration = new Configuration();
        this.effectorPackage = configuration.get("anticorruption.effectors.package");
    }

    @Override
    public Effector translateFrom(String id, String source) {
        return this.proxyProvider.provide(id);
    }

    @Override
    public String translateTo(String id, Effector effector) {
        return """
            package %s;
            import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
            import java.util.Map;
            public class %s implements Effector {
                @Override public String name() { return "%s"; }
                @Override public String tuning() { return "%s"; }
                @Override
                public Map<String, Object> fire(Map<String, Object> input) {
                    return Map.of(
                            "message", "Effector [%s] successfully executed."
                    );
                }
            }
            """
            .formatted(this.effectorPackage, id, id, effector.tuning(), id);
    }
}
