package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.util.List;

@ApplicationScoped
public class TopicRepository implements Repository<Topic> {
    private final Adapter<Topic, String> adapter;

    @Inject
    public TopicRepository(Adapter<Topic, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Topic find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Topic> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(String id, String source) {
        this.adapter.publish(id, source);
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
