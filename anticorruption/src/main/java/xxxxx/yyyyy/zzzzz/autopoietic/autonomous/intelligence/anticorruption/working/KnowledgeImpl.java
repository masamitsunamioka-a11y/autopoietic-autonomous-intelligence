package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.working;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ConversationScoped
public class KnowledgeImpl implements Knowledge, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Map<String, Object> memory;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Path knowledgeFile;

    @Inject
    public KnowledgeImpl(@Localic FileSystem fileSystem, JsonCodec jsonCodec) {
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        var configuration = new Configuration("anticorruption.yaml");
        var base = Path.of(configuration.get("anticorruption.memory.source"), "");
        this.knowledgeFile = base.resolve("knowledge.json");
        this.memory = new ConcurrentHashMap<>();
        this.load();
    }

    @Override
    public void encode(String key, Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.memory.put(String.format("%s@%s", key, LocalDateTime.now()), value);
        this.persist();
    }

    @Override
    public Object retrieve(String key) {
        return this.memory.entrySet().stream()
            .filter(x -> x.getKey().startsWith(key + "@"))
            .max(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    @Override
    public Map<String, Object> retrieve() {
        return Map.copyOf(this.memory);
    }

    @Override
    public void decay() {
        /// TODO: implement knowledge decay
    }

    private void load() {
        if (!this.fileSystem.exists(this.knowledgeFile)) {
            return;
        }
        Map<String, Object> map = this.jsonCodec.unmarshal(
            this.fileSystem.read(this.knowledgeFile, StandardCharsets.UTF_8));
        this.memory.putAll(map);
    }

    private void persist() {
        this.fileSystem.write(
            this.knowledgeFile,
            this.jsonCodec.marshal(this.memory),
            StandardCharsets.UTF_8);
    }
}
