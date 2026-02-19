package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemTopicAdapter implements Adapter<Topic, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemTopicAdapter.class);
    private final Configuration configuration;
    private final Translator<Topic, String> translator;
    private final FileSystem fileSystem;

    @Inject
    public FileSystemTopicAdapter(Translator<Topic, String> translator,
                                  @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    @Override
    public Topic fetch(String id) {
        return this.translator.translateFrom(
                id,
                this.fileSystem.read(
                        Paths.get(this.topicsSource().toString(), Util.toSnakeCase(id) + ".json"),
                        StandardCharsets.UTF_8
                ));
    }

    @Override
    public List<Topic> fetchAll() {
        return this.fileSystem.walk(this.topicsSource())
                .map(x -> x.replaceAll(".*/|\\.json$", ""))
                .map(this::fetch)
                .toList();
    }

    @Override
    public void publish(String id, String source) {
        this.fileSystem.write(
                Paths.get(this.topicsSource().toString(), Util.toSnakeCase(id) + ".json"),
                source,
                StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
                Path.of(this.configuration.get("anticorruption.topics.source")
                        + "/" + Util.toSnakeCase(id) + ".json"));
    }

    private Path topicsSource() {
        String topicsSource = this.configuration.get("anticorruption.topics.source");
        return Path.of(topicsSource);
    }
}
