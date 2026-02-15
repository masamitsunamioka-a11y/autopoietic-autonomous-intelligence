package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class FileSystemPromptAdapter implements Adapter<String, String> {
    private final Configuration configuration;
    private final FileSystem fileSystem;

    public FileSystemPromptAdapter(@Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.fileSystem = fileSystem;
    }

    @Override
    public String fetch(String id) {
        return this.fileSystem.read(
                Paths.get(this.promptsSource().toString(), id),
                StandardCharsets.UTF_8
        );
    }

    private Path promptsSource() {
        String promptsSource = this.configuration.get("anticorruption.prompts.source");
        return Path.of(promptsSource);
    }
}
