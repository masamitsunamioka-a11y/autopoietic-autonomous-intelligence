package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemAgentAdapter implements Adapter<Agent, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemAgentAdapter.class);
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

    @Override
    public Agent fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.fileSystem.read(
                Paths.get(this.agentsSource().toString(), Util.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8
            ));
    }

    @Override
    public List<Agent> fetchAll() {
        return this.fileSystem.walk(this.agentsSource())
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.fileSystem.write(
            Paths.get(this.agentsSource().toString(), Util.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
            this.agentsSource().resolve(Util.toSnakeCase(id) + ".json"));
    }

    private Path agentsSource() {
        return Path.of(this.configuration.get("anticorruption.agents.source"), "");
    }
}
