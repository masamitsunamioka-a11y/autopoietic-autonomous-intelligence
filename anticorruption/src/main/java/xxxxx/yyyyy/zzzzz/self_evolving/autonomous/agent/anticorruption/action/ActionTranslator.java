package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.action;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class ActionTranslator implements Translator<Action<?>, String> {
    private final Configuration configuration;

    public ActionTranslator() {
        this.configuration = new Configuration("anticorruption.yaml");
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    @Override
    public Action<?> toInternal(String name, String codePath) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            Path absoluteSrcPath = Paths.get(codePath).toAbsolutePath();
            String targetDir = this.configuration.get("anticorruption.actions.target");
            Path absoluteTargetDir = Paths.get(targetDir, "classes").toAbsolutePath();
            if (!Files.exists(absoluteTargetDir)) {
                Files.createDirectories(absoluteTargetDir);
            }
            int result = compiler.run(null, null, null, "-d", absoluteTargetDir.toString(), absoluteSrcPath.toString());
            if (result != 0) {
                throw new IllegalStateException("[Evolution Error] Compilation failed for: " + name);
            }
            URL[] urls = {absoluteTargetDir.toUri().toURL()};
            try (URLClassLoader loader = new URLClassLoader(urls, Action.class.getClassLoader())) {
                String fullClassName = this.actionsPackage() + "." + name;
                Class<?> clazz = loader.loadClass(fullClassName);
                return (Action<?>) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Action hydration failed: " + name, e);
        }
    }

    @Override
    public String toExternal(String name, Action<?> action) {
        String packageName = this.actionsPackage();
        String evidenceKey = "evidence." + name;
        return """
                package %s;
                import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;
                import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Output;
                import java.util.Map;
                public class %s implements Action<Output> {
                    @Override public String label() { return "%s"; }
                    @Override public String description() { return "System-guaranteed evolutionary action with evidence marking."; }
                    @Override
                    public Output execute(Map<String, Object> input) {
                        return new Output() {
                            @Override public String message() { return "Action [%s] successfully executed."; }
                            @Override public Map<String, Object> updates() {
                                return Map.of("%s", "SUCCESS_AT_" + System.currentTimeMillis());
                            }
                        };
                    }
                }
                """.formatted(packageName, name, name, name, evidenceKey);
    }
}
