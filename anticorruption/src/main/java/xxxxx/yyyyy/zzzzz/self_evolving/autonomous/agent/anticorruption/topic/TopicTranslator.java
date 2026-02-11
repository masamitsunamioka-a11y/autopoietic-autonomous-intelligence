package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.topic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.DynamicProxyFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Util;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.Map;

@ApplicationScoped
public class TopicTranslator implements Translator<Topic, String> {
    private final DynamicProxyFactory dynamicProxyFactory;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TopicTranslator(Repository<Action<?>> actionRepository) {
        this.dynamicProxyFactory = new DynamicProxyFactory(actionRepository);
    }

    @Override
    public Topic toInternal(String name, String json) {
        try {
            String cleaned = Util.cleanJson(json);
            Map<String, Object> data = this.gson.fromJson(
                    cleaned,
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            );
            if (data == null || !data.containsKey("name")) {
                throw new RuntimeException("Topic definition error: 'name' missing in " + name);
            }
            return this.dynamicProxyFactory.create(Topic.class, data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Topic JSON: " + name, e);
        }
    }

    @Override
    public String toExternal(String name, Topic topic) {
        throw new UnsupportedOperationException();
    }
}
