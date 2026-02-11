package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.List;

@ApplicationScoped
public class TopicRepository implements Repository<Topic> {
    private final Adapter<Topic, String> adapter;

    @Inject
    public TopicRepository(Adapter<Topic, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public List<Topic> findAll() {
        return this.adapter.toInternal();
    }

    @Override
    public Topic findByName(String name) {
        return this.findAll().stream()
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Persistence Error: Topic not found: " + name));
    }

    @Override
    public void store(Topic topic) {
        this.adapter.toExternal(topic.name(), topic);
    }

    @Override
    public void store(String name, Topic topic) {
        this.adapter.toExternal(name, topic);
    }

    @Override
    public void store(String name, String json) {
        this.adapter.toExternal(name, json);
    }
}
