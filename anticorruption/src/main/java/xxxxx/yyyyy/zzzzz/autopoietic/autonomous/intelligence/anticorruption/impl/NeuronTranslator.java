package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;

@ApplicationScoped
public class NeuronTranslator implements Translator<Neuron, String> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronTranslator.class);
    private final ProxyProvider<Neuron> proxyProvider;

    @Inject
    public NeuronTranslator(ProxyProvider<Neuron> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Neuron translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Neuron neuron) {
        throw new UnsupportedOperationException();
    }
}
