package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.agent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Agent;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class AgentRepository implements Repository<Agent> {
    private static final Logger logger = LoggerFactory.getLogger(AgentRepository.class);
    private final Adapter<Agent, String> adapter;
    private final Translator<Agent, String> translator;
    private final Repository<Topic> topicRepository;

    @Inject
    public AgentRepository(Adapter<Agent, String> adapter,
                           Translator<Agent, String> translator,
                           Repository<Topic> topicRepository) {
        this.adapter = adapter;
        this.translator = translator;
        this.topicRepository = topicRepository;
        System.out.println();
    }

    @Override
    public List<Agent> findAll() {
        return this.adapter.toInternal();
    }

    @Override
    public Agent findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Search Error: Agent name cannot be null.");
        }
        final String normalized = name.replace("_", "").toLowerCase();
        return this.findAll().stream()
                .filter(Objects::nonNull)
                .filter(x -> x.name().replace("_", "").toLowerCase().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Persistence Error: Agent not found: " + name));
    }

    @Override
    public void store(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException(
                    "Persistence Error: Cannot store a null Agent. The source of the evolution must be verified.");
        }
        this.adapter.toExternal(agent.name(), agent);
        Optional.ofNullable(agent.topics())
                .ifPresent(x -> x.forEach(this.topicRepository::store));
    }

    @Override
    public void store(String name, Agent agent) {
        this.adapter.toExternal(name, agent);
    }

    @Override
    public void store(String name, String json) {
        this.store(this.translator.toInternal(name, json));
    }
}
