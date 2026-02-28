package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

@ApplicationScoped
public class AreaTranslator implements Translator<Area, String> {
    private static final Logger logger = LoggerFactory.getLogger(AreaTranslator.class);
    private final ProxyProvider<Area> proxyProvider;

    @Inject
    public AreaTranslator(ProxyProvider<Area> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Area translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Area area) {
        throw new UnsupportedOperationException();
    }
}
