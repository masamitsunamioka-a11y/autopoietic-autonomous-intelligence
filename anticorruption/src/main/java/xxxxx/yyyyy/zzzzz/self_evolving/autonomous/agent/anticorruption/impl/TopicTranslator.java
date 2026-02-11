package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.DynamicProxyFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.Map;

@ApplicationScoped
public class TopicTranslator implements Translator<Topic, String> {
    private final DynamicProxyFactory dynamicProxyFactory;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TopicTranslator(Repository<Topic> topicRepository) {
        this.dynamicProxyFactory = new DynamicProxyFactory(topicRepository);
    }

    @Override
    public Topic toInternal(String name, String json) {
        try {
            Map<String, Object> topic = this.gson.fromJson(
                    json,
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            );
            return this.dynamicProxyFactory.create(Topic.class, topic);
        } catch (Exception e) {
            throw new RuntimeException("[Translation Error] Failed to parse Topic JSON: " + name, e);
        }
    }

    @Override
    public String toExternal(String name, Topic topic) {
        throw new UnsupportedOperationException();
    }
}
