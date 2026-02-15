package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
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

    @Inject
    public FileSystemActionAdapter(Translator<Action, String> translator,
                                   @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    @Override
    public Action toInternal(String id) {
        String path = (id.contains("/") || id.contains("\\")) ? id : this.resolveFullSourcePath(id);
        return this.translator.toInternal(id, path);
    }

    @Override
    public List<Action> toInternal() {
        String fullActionsSource = Paths.get(
                this.actionsSource(),
                this.actionsPackage().replace(".", "/")).normalize().toString();
        if (!this.fileSystem.exists(fullActionsSource)) {
            throw new IllegalStateException("Directory not found: " + fullActionsSource);
        }
        return this.fileSystem.walk(fullActionsSource)
                .filter(path -> path.endsWith(".java"))
                .map(this::extractIdFromPath)
                .map(this::toInternal)
                .toList();
    }

    @Override
    public void toExternal(String id, String source) {
        /// @formatter:off
        Map<String, Object> meta =
                new Gson().fromJson(source, new TypeToken<Map<String, Object>>() {}.getType());
        /// @formatter:on
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
                this.resolveFullSourcePath(id),
                this.translator.toExternal(id, action),
                StandardCharsets.UTF_8);
    }

    private String actionsSource() {
        return this.configuration.get("anticorruption.actions.source");
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    private String resolveFullSourcePath(String id) {
        String packagePath = this.actionsPackage().replace(".", "/");
        String fileName = id.endsWith(".java") ? id : id + ".java";
        return Paths.get(this.actionsSource(), packagePath, fileName).toString();
    }

    private String extractIdFromPath(String path) {
        Path p = Paths.get(path);
        String fileName = p.getFileName().toString();
        return fileName.replace(".java", "");
    }
}
