package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

public class JsonObjectTranslator<I extends Entity, E extends Resource> implements Translator<I, E> {
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectTranslator.class);
    private final Serializer serializer;
    private final ProxyProvider<I> proxyProvider;

    public JsonObjectTranslator(Serializer serializer,
                                ProxyProvider<I> proxyProvider) {
        this.serializer = serializer;
        this.proxyProvider = proxyProvider;
    }

    @Override
    public I internalize(E resource) {
        return this.proxyProvider.provide(resource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E externalize(I object) {
        return (E) new FileResource(this.serializer.serialize(object));
    }
}
