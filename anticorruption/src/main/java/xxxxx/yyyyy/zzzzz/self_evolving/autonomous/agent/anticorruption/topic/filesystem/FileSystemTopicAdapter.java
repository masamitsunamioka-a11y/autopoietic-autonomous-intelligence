package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.topic.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.*;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileSystemTopicAdapter implements Adapter<Topic, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemTopicAdapter.class);
    private final Configuration configuration;
    private final Translator<Topic, String> translator;
    private final FileSystem fileSystem;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Inject
    public FileSystemTopicAdapter(Translator<Topic, String> translator,
                                  @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    private String sourceDir() {
        return this.configuration.get("anticorruption.topics.source");
    }

    @Override
    public List<Topic> toInternal() {
        String sourceDir = this.sourceDir();
        if (!this.fileSystem.exists(sourceDir)) {
            throw new IllegalStateException("Base directory not found: " + sourceDir);
        }
        return this.fileSystem.walk(sourceDir)
                .map(this::extractIdFromPath)
                .map(this::toInternal)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public Topic toInternal(String name) {
        String sourceDir = this.sourceDir();
        String path = Paths.get(sourceDir, Util.toSnakeCase(name) + ".json").toString();
        return this.translator.toInternal(name, this.fileSystem.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public void toExternal(String name, Topic topic) {
        String sourceDir = this.sourceDir();
        String path = Paths.get(sourceDir, Util.toSnakeCase(name) + ".json").toString();
        this.fileSystem.write(path, this.gson.toJson(this.flatten(topic)), StandardCharsets.UTF_8);
    }

    @Override
    public void toExternal(String name, String json) {
        String sourceDir = this.sourceDir();
        String path = Paths.get(sourceDir, Util.toSnakeCase(name) + ".json").toString();
        this.fileSystem.write(path, json, StandardCharsets.UTF_8);
    }

    private Map<String, Object> flatten(Topic topic) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (topic == null) return map;
        try {
            Method[] methods = Topic.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() == 0 && !method.getReturnType().equals(Void.TYPE)) {
                    Object value = method.invoke(topic);
                    map.put(method.getName(), value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Flattening failed for topic: " + topic.name(), e);
        }
        return map;
    }

    private String extractIdFromPath(String path) {
        String baseDir = this.sourceDir();
        return path.replace(baseDir, "")
                .replaceFirst("^[\\\\/]", "")
                .replaceFirst("\\.json$", "");
    }
}
