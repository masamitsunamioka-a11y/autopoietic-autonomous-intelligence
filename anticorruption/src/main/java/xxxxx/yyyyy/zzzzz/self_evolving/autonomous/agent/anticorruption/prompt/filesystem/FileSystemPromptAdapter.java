package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.prompt.filesystem;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Localic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class FileSystemPromptAdapter implements Adapter<String, String> {
    private final Configuration configuration;
    private final FileSystem fileSystem;

    public FileSystemPromptAdapter(@Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.fileSystem = fileSystem;
    }

    private String sourceDir() {
        return this.configuration.get("anticorruption.prompts.source");
    }

    @Override
    public List<String> toInternal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toInternal(String name) {
        String path = Paths.get(this.sourceDir(), name).toString();
        return this.fileSystem.read(path, StandardCharsets.UTF_8);
    }

    @Override
    public void toExternal(String name, String prompt) {
        throw new UnsupportedOperationException();
    }
}
