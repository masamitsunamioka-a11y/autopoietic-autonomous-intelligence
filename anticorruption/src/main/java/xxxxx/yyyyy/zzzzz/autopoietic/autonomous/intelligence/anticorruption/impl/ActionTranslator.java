package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@ApplicationScoped
public class ActionTranslator implements Translator<Action, String> {
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
            throw new IllegalStateException("Failed to compile: " + id);
        }
        return this.instantiate(target, id);
    }

    private Action instantiate(Path directory, String id) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (var x = new URLClassLoader(new URL[]{directory.toUri().toURL()}, classLoader)) {
            return Optional.of(x.loadClass(this.actionsPackage() + "." + id))
                    ///.filter(Action.class::isAssignableFrom)
                    .map(y -> {
                        try {
                            return (Action) y.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElseThrow(() -> new IllegalStateException("Not an Action: " + id));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
