package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.action.filesystem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.*;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

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

    private String getActionSrcDir() {
        return this.configuration.get("anticorruption.actions.source");
    }

    private String getActionPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    private String resolveFullSourcePath(String name) {
        String packagePath = this.getActionPackage().replace(".", "/");
        String fileName = name.endsWith(".java") ? name : name + ".java";
        return Paths.get(this.getActionSrcDir(), packagePath, fileName).toString();
    }

    @Override
    public List<Action<?>> toInternal() {
        String packagePath = this.getActionPackage().replace(".", "/");
        Path searchPath = Paths.get(this.getActionSrcDir(), packagePath).normalize();
        String searchDir = searchPath.toString();
        if (!this.fileSystem.exists(searchDir)) {
            return java.util.Collections.emptyList();
        }
        return this.fileSystem.walk(searchDir)
                .filter(path -> path.endsWith(".java"))
                .map(this::extractIdFromPath)
                .<Action<?>>map(this::toInternal)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Action<?> toInternal(String name) {
        String path = (name.contains("/") || name.contains("\\")) ? name : this.resolveFullSourcePath(name);
        return this.translator.toInternal(name, path);
    }

    @Override
    public void toExternal(String name, Action<?> action) {
        this.toExternal(name, this.translator.toExternal(name, action));
    }

    @Override
    public void toExternal(String name, String code) {
        String fullPath = this.resolveFullSourcePath(name);
        this.fileSystem.write(fullPath, code, StandardCharsets.UTF_8);
    }

    private String extractIdFromPath(String path) {
        Path p = Paths.get(path);
        String fileName = p.getFileName().toString();
        return fileName.replace(".java", "");
    }
}
