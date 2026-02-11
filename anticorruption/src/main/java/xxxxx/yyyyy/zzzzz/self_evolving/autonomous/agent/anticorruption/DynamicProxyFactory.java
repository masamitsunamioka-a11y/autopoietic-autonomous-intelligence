package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public class DynamicProxyFactory {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Repository<Topic> topicRepository;

    @Inject
    public DynamicProxyFactory(Repository<Topic> topicRepository) {
        this.topicRepository = topicRepository;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz, Map<String, Object> data) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "toString" -> GSON.toJson(data);
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> arguments != null && arguments.length == 1 && proxy == arguments[0];
                case "instructions" -> {
                    if (arguments != null && arguments.length == 1) {
                        yield data.put("instructions", arguments[0]);
                    }
                    yield data.get("instructions");
                }
                case "topics" -> {
                    yield ((List<?>) data.get("topics")).stream()
                            .map(x -> (String) x)
                            .map(topicRepository::findByName)
                            .toList();
                }
                default -> {
                    yield data.get(method.getName());
                }
            };
        });
    }
}
