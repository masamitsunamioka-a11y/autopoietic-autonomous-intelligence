package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;

@ApplicationScoped
public class ModuleTranslator implements Translator<Module, String> {
    private static final Logger logger = LoggerFactory.getLogger(ModuleTranslator.class);
    private final ProxyProvider<Module> proxyProvider;

    @Inject
    public ModuleTranslator(ProxyProvider<Module> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Module translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Module module) {
        throw new UnsupportedOperationException();
    }
}
