package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.DynamicProxyFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Util;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Agent;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class AgentTranslator implements Translator<Agent, String> {
    private static final Logger logger = LoggerFactory.getLogger(AgentTranslator.class);
    private final DynamicProxyFactory dynamicProxyFactory;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Repository<Topic> topicRepository;

    @Inject
    public AgentTranslator(Repository<Topic> topicRepository,
                           Repository<Action<?>> actionRepository) {
        this.topicRepository = topicRepository;
        this.dynamicProxyFactory = new DynamicProxyFactory(actionRepository);
    }

    @Override
    public Agent toInternal(String name, String json) {
        try {
            Map<String, Object> agentData = this.gson.fromJson(json, new TypeToken<Map<String, Object>>() {
            }.getType());
            if (agentData == null) {
                throw new IllegalArgumentException("Translation Error: Parsed agent data is null for " + name);
            }
            agentData.put("name", Util.toCamelCase(name));
            if (agentData.containsKey("topics") && agentData.get("topics") instanceof List<?> topicNames) {
                List<Topic> resolvedTopics = topicNames.stream()
                        .map(Object::toString)
                        .map(this.topicRepository::findByName)
                        .filter(Objects::nonNull)
                        .toList();
                agentData.put("topics", resolvedTopics);
            }
            return this.dynamicProxyFactory.create(Agent.class, agentData);
        } catch (Exception e) {
            throw new RuntimeException("Translation Error: Failed to parse Agent JSON for " + name, e);
        }
    }

    @Override
    public String toExternal(String name, Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Translation Error: Cannot translate a null Agent.");
        }
        final Map<String, Object> pureMap = new LinkedHashMap<>();
        pureMap.put("name", agent.name());
        pureMap.put("label", agent.label());
        pureMap.put("description", agent.description());
        pureMap.put("instructions", agent.instructions());
        if (agent.topics() != null) {
            pureMap.put("topics", agent.topics().stream()
                    .filter(Objects::nonNull)
                    .map(Topic::name)
                    .toList());
        }
        return this.gson.toJson(pureMap);
    }
}
