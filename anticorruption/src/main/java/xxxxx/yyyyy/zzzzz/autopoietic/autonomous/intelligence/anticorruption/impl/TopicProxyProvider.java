package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.JsonParser;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TopicProxyProvider implements ProxyProvider<Topic> {
    private final Repository<Action> actionRepository;
    private final JsonParser jsonParser;

    @Inject
    public TopicProxyProvider(Repository<Action> actionRepository,
                              JsonParser jsonParser) {
        this.actionRepository = actionRepository;
        this.jsonParser = jsonParser;
    }

    /// @SuppressWarnings("unchecked")
    @Override
    public Topic provide(String json) {
        Map<String, Object> attributes = this.jsonParser.from(json);
        Class<Topic> topicClass =
                (Class<Topic>) ((ParameterizedType)
                        getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return (Topic) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{topicClass},
                (proxy, method, arguments) -> {
                    return switch (method.getName()) {
                        case "toString" -> this.jsonParser.to(attributes);
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> arguments != null && arguments.length == 1 && proxy == arguments[0];
                        case "actions" -> {
                            yield ((List<?>) attributes.get("actions")).stream()
                                    .map(x -> (String) x)
                                    .map(this.actionRepository::find)
                                    .toList();
                        }
                        default -> attributes.get(method.getName());
                    };
                }
        );
    }
}
