package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Receptor;

@ApplicationScoped
public class ReceptorTranslator implements Translator<Receptor, String> {
    private static final Logger logger = LoggerFactory.getLogger(ReceptorTranslator.class);
    private final ProxyProvider<Receptor> proxyProvider;

    @Inject
    public ReceptorTranslator(ProxyProvider<Receptor> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Receptor translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Receptor receptor) {
        throw new UnsupportedOperationException();
    }
}
