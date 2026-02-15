package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class ActionTranslator implements Translator<Action, String> {
    private final Configuration configuration;

    public ActionTranslator() {
        this.configuration = new Configuration("anticorruption.yaml");
    }

    private String actionsPackage() {
        return this.configuration.get("anticorruption.actions.package");
    }

    @Override
    public Action toInternal(String id, String codePath) {
        try {
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            Path absoluteSrcPath = Paths.get(codePath).toAbsolutePath();
            String targetDir = this.configuration.get("anticorruption.actions.target");
            Path absoluteTargetDir = Paths.get(targetDir, "classes").toAbsolutePath();
            if (!Files.exists(absoluteTargetDir)) {
                Files.createDirectories(absoluteTargetDir);
            }
            String strategy = this.configuration.get("anticorruption.actions.compiler.classpath.strategy");
            String classpath;
            if ("manual".equals(strategy)) {
                classpath = this.configuration.get("anticorruption.actions.compiler.classpath.value");
            } else {
                classpath = System.getProperty("java.class.path");
            }
            int result = javaCompiler.run(null, null, null,
                    "-d", absoluteTargetDir.toString(),
                    "-classpath", classpath,
                    absoluteSrcPath.toString());
            if (result != 0) {
                throw new IllegalStateException("[Evolution Error] Compilation failed for Action: " + id);
            }
            String fullClassName = this.actionsPackage() + "." + id;
            try (URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{absoluteTargetDir.toUri().toURL()},
                    this.getClass().getClassLoader()
            )) {
                Class<?> clazz = classLoader.loadClass(fullClassName);
                if (Action.class.isAssignableFrom(clazz)) {
                    return (Action) clazz.getDeclaredConstructor().newInstance();
                } else {
                    throw new IllegalStateException("Class " + fullClassName + " does not implement Action interface.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Action hydration failed: " + id, e);
        }
    }

    @Override
    public String toExternal(String id, Action action) {
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
}
