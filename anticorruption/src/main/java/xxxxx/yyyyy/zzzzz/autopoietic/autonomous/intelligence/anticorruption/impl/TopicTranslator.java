package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

@ApplicationScoped
public class TopicTranslator implements Translator<Topic, String> {
    private static final Logger logger = LoggerFactory.getLogger(TopicTranslator.class);
    private final ProxyProvider<Topic> proxyProvider;

    @Inject
    public TopicTranslator(ProxyProvider<Topic> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Topic translateFrom(String id, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String translateTo(String id, Topic topic) {
        throw new UnsupportedOperationException();
    }
}
