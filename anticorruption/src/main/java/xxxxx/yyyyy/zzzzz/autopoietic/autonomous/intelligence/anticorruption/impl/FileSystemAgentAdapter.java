package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemAgentAdapter implements Adapter<Agent, String> {
    private final Configuration configuration;
    private final Translator<Agent, String> translator;
    private final FileSystem fileSystem;

    @Inject
    public FileSystemAgentAdapter(Translator<Agent, String> translator,
                                  @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    private String agentsSource() {
        return this.configuration.get("anticorruption.agents.source");
    }

    @Override
    public Agent toInternal(String id) {
        String path = Paths.get(this.agentsSource(), Util.toSnakeCase(id) + ".json").toString();
        return this.translator.toInternal(id, this.fileSystem.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public List<Agent> toInternal() {
        String agentsSource = this.agentsSource();
        if (!this.fileSystem.exists(agentsSource)) {
            throw new IllegalStateException("Directory not found: " + agentsSource);
        }
        return this.fileSystem.walk(agentsSource)
                .map(this::extractIdFromPath)
                .map(this::toInternal)
                .toList();
    }

    @Override
    public void toExternal(String id, String source) {
        String path = Paths.get(this.agentsSource(), Util.toSnakeCase(id) + ".json").toString();
        this.fileSystem.write(path, source, StandardCharsets.UTF_8);
    }

    private String extractIdFromPath(String path) {
        String baseDir = this.agentsSource();
        return path.replace(baseDir, "")
                .replaceFirst("^[\\\\/]", "")
                .replaceFirst("\\.json$", "");
    }
}
