package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemActionAdapter implements Adapter<Action<?>, String> {
    private final Configuration configuration;
    private final Translator<Action<?>, String> translator;
    private final FileSystem fileSystem;

    @Inject
    public FileSystemActionAdapter(Translator<Action<?>, String> translator,
                                   @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    @Override
    public Action<?> toInternal(String id) {
        String path = (id.contains("/") || id.contains("\\")) ? id : this.resolveFullSourcePath(id);
        return this.translator.toInternal(id, path);
    }

    @Override
    public List<Action<?>> toInternal() {
        String actionsFullSource = Paths.get(
                this.actionsSource(),
                this.actionsPackage().replace(".", "/")).normalize().toString();
        if (!this.fileSystem.exists(actionsFullSource)) {
            throw new IllegalStateException("Directory not found: " + actionsFullSource);
        }
        return this.fileSystem.walk(actionsFullSource)
                .filter(path -> path.endsWith(".java"))
                .map(this::extractIdFromPath)
                .<Action<?>>map(this::toInternal)
                .toList();
    }

    @Override
    public void toExternal(String id, String source) {
        this.fileSystem.write(
                this.resolveFullSourcePath(id),
                this.translator.toExternal(id, null),
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
