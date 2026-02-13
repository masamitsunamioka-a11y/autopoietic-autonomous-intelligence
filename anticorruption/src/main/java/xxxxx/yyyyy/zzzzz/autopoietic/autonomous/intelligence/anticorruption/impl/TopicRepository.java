package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.util.List;

@ApplicationScoped
public class TopicRepository implements Repository<Topic> {
    private final Adapter<Topic, String> adapter;
    private final Configuration configuration;
    private final FileSystem fileSystem;

    @Inject
    public TopicRepository(Adapter<Topic, String> adapter,
                           @Localic FileSystem fileSystem) {
        this.adapter = adapter;
        this.configuration = new Configuration("anticorruption.yaml");
        this.fileSystem = fileSystem;
    }

    @Override
    public Topic find(String id) {
        return this.findAll().stream()
                .filter(x -> x.name().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Persistence Error: Topic not found: " + id));
    }

    @Override
    public List<Topic> findAll() {
        return this.adapter.toInternal();
    }

    @Override
    public void store(String id, String source) {
        this.adapter.toExternal(id, source);
    }

    @Override
    public void remove(String id) {
        this.fileSystem.delete(
                this.configuration.get("anticorruption.topics.source")
                        + "/" + Util.toSnakeCase(id) + ".json");
    }
}
