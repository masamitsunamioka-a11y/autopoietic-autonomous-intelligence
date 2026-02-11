package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.impl;

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
                throw new IllegalStateException("[Evolution Error] Compilation failed for Action: " + name);
            }
            String fullClassName = this.actionsPackage() + "." + name;
            try (URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{absoluteTargetDir.toUri().toURL()},
                    this.getClass().getClassLoader()
            )) {
                Class<?> clazz = classLoader.loadClass(fullClassName);
                if (Action.class.isAssignableFrom(clazz)) {
                    return (Action<?>) clazz.getDeclaredConstructor().newInstance();
                } else {
                    throw new IllegalStateException("Class " + fullClassName + " does not implement Action interface.");
                }
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
