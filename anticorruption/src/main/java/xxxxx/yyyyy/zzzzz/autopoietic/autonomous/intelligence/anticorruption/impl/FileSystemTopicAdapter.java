package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemTopicAdapter implements Adapter<Topic, String> {
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

    private String topicsSource() {
        return this.configuration.get("anticorruption.topics.source");
    }

    @Override
    public Topic toInternal(String id) {
        String path = Paths.get(this.topicsSource(), Util.toSnakeCase(id) + ".json").toString();
        return this.translator.toInternal(id, this.fileSystem.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public List<Topic> toInternal() {
        String topicsSource = this.topicsSource();
        if (!this.fileSystem.exists(topicsSource)) {
            throw new IllegalStateException("Directory not found: " + topicsSource);
        }
        return this.fileSystem.walk(topicsSource)
                .map(this::extractIdFromPath)
                .map(this::toInternal)
                .toList();
    }

    @Override
    public void toExternal(String id, String source) {
        String path = Paths.get(this.topicsSource(), Util.toSnakeCase(id) + ".json").toString();
        this.fileSystem.write(path, source, StandardCharsets.UTF_8);
    }

    private String extractIdFromPath(String path) {
        String baseDir = this.topicsSource();
        return path.replace(baseDir, "")
                .replaceFirst("^[\\\\/]", "")
                .replaceFirst("\\.json$", "");
    }
}
