package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Configuration;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@ApplicationScoped
public class ActionCompiler implements Compiler {
    private final Configuration configuration;

    public ActionCompiler() {
        this.configuration = new Configuration("anticorruption.yaml");
    }

    @Override
    public int compile(Path source, Path target) {
        try {
            if (Files.notExists(target)) {
                Files.createDirectories(target);
            }
            String[] javaFiles = this.javaFiles(source);
            if (javaFiles.length == 0) {
                return 0;
            }
            String strategy = this.configuration.get("anticorruption.actions.compiler.classpath.strategy");
            String classpath = "manual".equals(strategy)
                    ? this.configuration.get("anticorruption.actions.compiler.classpath.value")
                    : System.getProperty("java.class.path");
            return ToolProvider.getSystemJavaCompiler().run(null, null, null,
                    "-d", target.toString(),
                    "-classpath", classpath,
                    "-Xlint:none",
                    String.join(" ", javaFiles));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String[] javaFiles(Path source) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(x -> x.endsWith(".java"))
                    .toArray(String[]::new);
        }
    }
}
