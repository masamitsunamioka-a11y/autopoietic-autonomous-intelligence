package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

@ApplicationScoped
public class NeuronTranslator implements Translator<Neuron, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronTranslator.class);
    private final ProxyProvider<Neuron> proxyProvider;
    private final Serializer serializer;

    @Inject
    public NeuronTranslator(ProxyProvider<Neuron> proxyProvider,
                            Serializer serializer) {
        this.proxyProvider = proxyProvider;
        this.serializer = serializer;
    }

    @Override
    public Neuron translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Engravable engravable) {
        return this.serializer.serialize(engravable);
    }
}
