package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Schema;

@ApplicationScoped
public class SchemaTranslator implements Translator<Schema, String> {
    private static final Logger logger = LoggerFactory.getLogger(SchemaTranslator.class);
    private final ProxyProvider<Schema> proxyProvider;

    @Inject
    public SchemaTranslator(ProxyProvider<Schema> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Schema translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Schema schema) {
        throw new UnsupportedOperationException();
    }
}
