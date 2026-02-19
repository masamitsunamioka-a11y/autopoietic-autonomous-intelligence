package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Util.actualTypeArguments;

@ApplicationScoped
public class AgentProxyProvider implements ProxyProvider<Agent> {
    private static final Logger logger = LoggerFactory.getLogger(AgentProxyProvider.class);
    private final Repository<Topic> topicRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public AgentProxyProvider(Repository<Topic> topicRepository,
                              JsonCodec jsonCodec) {
        this.topicRepository = topicRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalAgent(
            String name,
            String label,
            String description,
            String instructions,
            List<String> topics) {
    }

    @Override
    public Agent provide(String json) {
        var reference = new AtomicReference<InternalAgent>(this.jsonCodec.unmarshal(json, InternalAgent.class));
        /// @formatter:off
        return (Agent) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                InternalAgent agent = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(agent);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "topics" -> {
                        if (args == null || args.length == 0) {
                            yield agent.topics().stream()
                                .map(this.topicRepository::find)
                                .distinct()
                                .toList();
                        } else {
                            String name = ((Topic) args[0]).name();
                            reference.set(new InternalAgent(
                                agent.name(),
                                agent.label(),
                                agent.description(),
                                agent.instructions(),
                                Stream.concat(agent.topics().stream(), Stream.of(name))
                                    .distinct()
                                    .toList()
                            ));
                            yield null;
                        }
                    }
                    default -> InternalAgent.class.getMethod(method.getName()).invoke(agent);
                };
            }
        );
        /// @formatter:on
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
