package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
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
    private final Map<String, Path> sources;

    @Inject
    public TemplateRepository(Storage storage) {
        this.storage = storage;
        var configuration = new Configuration().anticorruption();
        this.sources = new HashMap<>();
        this.resolve(Path.of(configuration.get("synaptic.function.source"), ""));
        this.resolve(Path.of(configuration.get("synaptic.shared.source"), ""));
    }

    @Override
    public String find(String id) {
        return this.storage.read(this.sources.get(id), StandardCharsets.UTF_8);
    }

    /// @formatter:off
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
    @Override
    public void removeAll(List<String> ids) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean exists(String id) {
        throw new UnsupportedOperationException();
    }
    /// @formatter:on
    private void resolve(Path path) {
        this.storage.list(path)
            .forEach(x -> this.sources.put(x.getFileName().toString(), x));
    }
}
