package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TemplateRepository implements Repository<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);
    private final Storage storage;
    private final Map<String, Path> paths;

    @Inject
    public TemplateRepository(Storage storage) {
        this.storage = storage;
        var configuration = new Configuration();
        this.paths = new HashMap<>();
        this.resolve(Path.of(
            configuration.get("anticorruption.encodings.phase"), ""));
        this.resolve(Path.of(
            configuration.get("anticorruption.encodings.shared"), ""));
    }

    private void resolve(Path dir) {
        this.storage.list(dir).forEach(path -> {
            var file = Path.of(path);
            this.paths.put(
                file.getFileName().toString(), file);
        });
    }

    @Override
    public String find(String id) {
        return this.storage.read(
            this.paths.get(id), StandardCharsets.UTF_8);
    }

    @Override
    public List<String> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void store(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException();
    }
}
