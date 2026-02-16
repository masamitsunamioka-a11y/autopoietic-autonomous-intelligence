package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;

@ApplicationScoped
public class AgentTranslator implements Translator<Agent, String> {
    private static final Logger logger = LoggerFactory.getLogger(AgentTranslator.class);
    private final ProxyProvider<Agent> proxyProvider;

    @Inject
    public AgentTranslator(ProxyProvider<Agent> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Agent translateFrom(String id, String source) {
        return this.proxyProvider.provide(source);
    }
}
