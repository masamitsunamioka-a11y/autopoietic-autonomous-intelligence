package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.topic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.util.List;

@ApplicationScoped
public class TopicRepository implements Repository<Topic> {
    private final Adapter<Topic, String> adapter;
    private final Translator<Topic, String> translator;

    @Inject
    public TopicRepository(Adapter<Topic, String> adapter,
                           Translator<Topic, String> translator) {
        this.adapter = adapter;
        this.translator = translator;
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
                .orElse(null);
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
        this.store(this.translator.toInternal(name, json));
    }
}
