package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

@ApplicationScoped
public class TopicTranslator implements Translator<Topic, String> {
    private final ProxyProvider<Topic> proxyProvider;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TopicTranslator(ProxyProvider<Topic> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Topic toInternal(String name, String json) {
        return this.proxyProvider.provide(json);
    }

    @Override
    public String toExternal(String name, Topic topic) {
        throw new UnsupportedOperationException();
    }
}
