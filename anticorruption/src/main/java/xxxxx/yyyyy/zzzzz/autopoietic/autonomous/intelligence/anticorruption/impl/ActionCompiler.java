package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class ActionCompiler implements Compiler {
    private static final Logger logger = LoggerFactory.getLogger(ActionCompiler.class);
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
            int result = ToolProvider.getSystemJavaCompiler().run(
                null, null, null,
                this.arguments(this.options(target), this.sources(source)));
            if (result != 0) {
                throw new IllegalStateException();
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String[] arguments(List<String> options, List<String> sources) {
        return Stream.of(options, sources)
            .flatMap(List::stream)
            .toArray(String[]::new);
    }

    private List<String> options(Path target) {
        return List.of(
            "-d", target.toString(),
            "-classpath", this.classpath(),
            "-Xlint:none");
    }

    private String classpath() {
        var strategy = this.configuration.get("anticorruption.actions.compiler.classpath.strategy");
        return "manual".equals(strategy)
            ? this.configuration.get("anticorruption.actions.compiler.classpath.value")
            : System.getProperty("java.class.path");
    }

    private List<String> sources(Path source) {
        try (var stream = Files.walk(source)) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(x -> x.endsWith(".java"))
                .peek(x -> logger.trace("java: {}", x))
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
