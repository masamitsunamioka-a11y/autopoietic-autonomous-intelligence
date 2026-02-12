package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AgentProxyProvider implements ProxyProvider<Agent> {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Repository<Topic> topicRepository;

    @Inject
    public AgentProxyProvider(Repository<Topic> topicRepository) {
        this.topicRepository = topicRepository;
    }

    /// @SuppressWarnings("unchecked")
    @Override
    public Agent provide(String json) {
        Map<String, Object> attributes =
                GSON.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
        Class<Agent> agentClass =
                (Class<Agent>) ((ParameterizedType)
                        getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return (Agent) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{agentClass},
                (proxy, method, arguments) -> {
                    return switch (method.getName()) {
                        case "toString" -> GSON.toJson(attributes);
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> arguments != null && arguments.length == 1 && proxy == arguments[0];
                        case "topics" -> {
                            yield ((List<?>) attributes.get("topics")).stream()
                                    .map(x -> (String) x)
                                    .map(this.topicRepository::findByName)
                                    .toList();
                        }
                        default -> attributes.get(method.getName());
                    };
                }
        );
    }
}
