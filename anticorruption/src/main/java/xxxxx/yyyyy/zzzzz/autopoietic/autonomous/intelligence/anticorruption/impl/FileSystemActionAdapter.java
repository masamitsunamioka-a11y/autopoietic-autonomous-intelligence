package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.JsonParser;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileSystemActionAdapter implements Adapter<Action, String> {
    private final Configuration configuration;
    private final Translator<Action, String> translator;
    private final FileSystem fileSystem;
    private final JsonParser jsonParser;

    @Inject
    public FileSystemActionAdapter(Translator<Action, String> translator,
                                   @Localic FileSystem fileSystem,
                                   JsonParser jsonParser) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
        this.jsonParser = jsonParser;
    }

    @Override
    public Action fetch(String id) {
        return this.translator.translateFrom(id, this.javaPath(id).toString());
    }

    @Override
    public List<Action> fetchAll() {
        return this.fileSystem.walk(this.actionsSource())
                .filter(path -> path.endsWith(".java"))
                .map(this::extractId)
                .map(this::fetch)
                .toList();
    }

    @Override
    public void publish(String id, String source) {
        Map<String, Object> meta = this.jsonParser.from(source);
        String label = (String) meta.getOrDefault("label", id);
        String description = (String) meta.getOrDefault("description", "Autopoietic evolution generated action.");
        Action action = new Action() {
            /// @formatter:off
            @Override public String name() { return id; }
            @Override public String label() { return label; }
            @Override public String description() { return description; }
            /// @formatter:on
            @Override
            public Map<String, Object> execute(Map<String, Object> input) {
                return Map.of();
            }
        };
        this.fileSystem.write(
                this.javaPath(id),
                this.translator.translateTo(id, action),
                StandardCharsets.UTF_8
        );
    }

    private Path actionsSource() {
        String actionsSource = this.configuration.get("anticorruption.actions.source");
        return Path.of(actionsSource);
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    private Path javaPath(String id) {
        return Paths.get(
                this.actionsSource().toString(),
                this.actionsPackage().replace(".", "/"),
                id.endsWith(".java")
                        ? id
                        : id + ".java"
        );
    }

    private String extractId(String path) {
        return Paths.get(path)
                .getFileName()
                .toString()
                .replace(".java", "");
    }
}
