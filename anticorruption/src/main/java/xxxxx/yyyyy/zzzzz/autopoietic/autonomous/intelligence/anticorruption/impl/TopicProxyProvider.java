package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Util.actualTypeArguments;

@ApplicationScoped
public class TopicProxyProvider implements ProxyProvider<Topic> {
    private static final Logger logger = LoggerFactory.getLogger(TopicProxyProvider.class);
    private final Repository<Action> actionRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public TopicProxyProvider(Repository<Action> actionRepository,
                              JsonCodec jsonCodec) {
        this.actionRepository = actionRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalTopic(
            String name,
            String label,
            String description,
            String instructions,
            List<String> actions) {
    }

    @Override
    public Topic provide(String json) {
        var reference = new AtomicReference<InternalTopic>(this.jsonCodec.unmarshal(json, InternalTopic.class));
        /// @formatter:off
        return (Topic) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                InternalTopic topic = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(topic);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "actions" -> {
                        if (args == null || args.length == 0) {
                            yield topic.actions().stream()
                                .map(this.actionRepository::find)
                                .distinct()
                                .toList();
                        } else {
                            String name = ((Action) args[0]).name();
                            reference.set(new InternalTopic(
                                topic.name(),
                                topic.label(),
                                topic.description(),
                                topic.instructions(),
                                Stream.concat(topic.actions().stream(), Stream.of(name))
                                    .distinct()
                                    .toList()
                            ));
                            yield null;
                        }
                    }
                    default -> InternalTopic.class.getMethod(method.getName()).invoke(topic);
                };
            }
        );
        /// @formatter:on
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
