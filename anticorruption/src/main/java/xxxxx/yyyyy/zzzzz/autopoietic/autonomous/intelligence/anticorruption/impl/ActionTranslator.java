package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

@ApplicationScoped
public class ActionTranslator implements Translator<Action, String> {
    private static final Logger logger = LoggerFactory.getLogger(ActionTranslator.class);
    private final Configuration configuration;

    public ActionTranslator() {
        this.configuration = new Configuration("anticorruption.yaml");
    }

    @Override
    public Action translateFrom(String id, String source) {
        try (URLClassLoader loader = this.urlClassLoader()) {
            return (Action) loader.loadClass(source).getConstructor().newInstance();
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private URLClassLoader urlClassLoader() {
        try {
            return new URLClassLoader(
                new URL[]{this.actionsTarget().toUri().toURL()},
                Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String translateTo(String id, Action action) {
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
            """
            .formatted(this.actionsPackage(), id, id, action.label(), action.description(), id);
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    private Path actionsTarget() {
        String actionsTarget = this.configuration.get("anticorruption.actions.target");
        return Path.of(actionsTarget);
    }
}
