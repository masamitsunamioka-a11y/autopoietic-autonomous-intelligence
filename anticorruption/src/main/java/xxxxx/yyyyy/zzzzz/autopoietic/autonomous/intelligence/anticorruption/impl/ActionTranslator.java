package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.SpinLock;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@ApplicationScoped
public class ActionTranslator implements Translator<Action, String> {
    private static final Logger logger = LoggerFactory.getLogger(ActionTranslator.class);
    private final Configuration configuration;
    private final Compiler compiler;

    public ActionTranslator(Compiler compiler) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.compiler = compiler;
    }

    @Override
    public Action translateFrom(String id, String source) {
        Path target = Paths.get(
                        this.configuration.get("anticorruption.actions.target"),
                        "classes"
                )
                .toAbsolutePath();
        if (this.compiler.compile(Paths.get(source), target) != 0) {
            throw new RuntimeException("Compilation failed for Evolution ID: " + id);
        }
        Path classFilePath = target.resolve(this.actionsPackage().replace('.', '/') + "/" + id + ".class");
        new SpinLock().await(() -> Files.exists(classFilePath), 20, 100);
        try (URLClassLoader loader = new URLClassLoader(
                new URL[]{target.toUri().toURL()},
                Thread.currentThread().getContextClassLoader())) {
            Class<?> clazz = loader.loadClass(this.actionsPackage() + "." + id);
            return Optional.ofNullable(clazz.getDeclaredConstructor().newInstance())
                    .filter(Action.class::isInstance)
                    .map(Action.class::cast)
                    .orElseThrow(() -> new IllegalStateException("Class mismatch: " + id + " is not an Action."));
        } catch (Exception e) {
            logger.error("Architectural failure during Action incarnation for ID: {}", id, e);
            throw new RuntimeException("Evolutionary transition failed: " + id, e);
        }
    }

    @Override
    public String translateTo(String id, Action action) {
        String packageName = this.actionsPackage();
        String evidenceKey = "evidence." + id;
        return """
                package %s;
                import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;
                import java.util.Map;
                public class %s implements Action {
                    @Override public String name() { return "%s"; }
                    @Override public String label() { return "%s"; }
                    @Override public String description() { return "%s"; }
                    @Override
                    public Map<String, Object> execute(Map<String, Object> input) {
                        return Map.of(
                                "message", "Action [%s] successfully executed."
                        );
                    }
                }
                """.formatted(packageName, id, id, action.label(), action.description(), evidenceKey);
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }
}
