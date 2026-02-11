package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
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
    private final Repository<Topic> topicRepository;

    @Inject
    public AgentRepository(Adapter<Agent, String> adapter,
                           Repository<Topic> topicRepository) {
        this.adapter = adapter;
        this.topicRepository = topicRepository;
    }

    @Override
    public List<Agent> findAll() {
        return this.adapter.toInternal();
    }

    @Override
    public Agent findByName(String name) {
        return this.findAll().stream()
                .filter(Objects::nonNull)
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Persistence Error: Agent not found: " + name));
    }

    @Override
    public void store(Agent agent) {
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
        this.adapter.toExternal(name, json);
    }
}
