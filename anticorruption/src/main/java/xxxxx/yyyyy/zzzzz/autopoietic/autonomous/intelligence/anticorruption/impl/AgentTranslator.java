package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class AgentTranslator implements Translator<Agent, String> {
    private static final Logger logger = LoggerFactory.getLogger(AgentTranslator.class);
    private final ProxyProvider<Agent> proxyProvider;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Inject
    public AgentTranslator(ProxyProvider<Agent> proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    @Override
    public Agent toInternal(String name, String json) {
        return this.proxyProvider.provide(json);
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
