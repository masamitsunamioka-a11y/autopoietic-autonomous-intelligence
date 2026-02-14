package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

@ApplicationScoped
public class TopicTranslator implements Translator<Topic, String> {
    private final ProxyProvider<Topic> proxyProvider;

    @Inject
    public TopicTranslator(ProxyProvider<Topic> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Topic toInternal(String id, String source) {
        return this.proxyProvider.provide(source);
    }

    @Override
    public String toExternal(String id, Topic topic) {
        throw new UnsupportedOperationException();
    }
}
