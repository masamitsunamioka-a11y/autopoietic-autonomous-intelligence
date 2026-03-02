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
    private final Map<String, String> templates;

    @Inject
    public TemplateRepository(Storage fs) {
        var configuration = new Configuration();
        var phaseSource = Path.of(
            configuration.get("anticorruption.encodings.phase"), "");
        var sharedSource = Path.of(
            configuration.get("anticorruption.encodings.shared"), "");
        this.templates = new HashMap<>();
        this.load(fs, phaseSource);
        this.load(fs, sharedSource);
    }

    private void load(Storage fs, Path dir) {
        fs.list(dir).forEach(path -> {
            var file = Path.of(path);
            this.templates.put(
                file.getFileName().toString(),
                fs.read(file, StandardCharsets.UTF_8));
        });
    }

    @Override
    public String find(String id) {
        var content = this.templates.get(id);
        if (content == null) {
            throw new IllegalArgumentException("Template not found: " + id);
        }
        return content;
    }

    @Override
    public List<String> findAll() {
        return List.copyOf(this.templates.values());
    }

    @Override
    public boolean exists(String id) {
        return this.templates.containsKey(id);
    }

    @Override
    public void store(String content) {
        throw new UnsupportedOperationException("Templates are read-only");
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException("Templates are read-only");
    }
}
