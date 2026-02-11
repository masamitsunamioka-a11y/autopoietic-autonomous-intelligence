package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.agent.filesystem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.*;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Agent;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Topic;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class FileSystemAgentAdapter implements Adapter<Agent, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemAgentAdapter.class);
    private final Configuration configuration;
    private final Translator<Agent, String> translator;
    private final FileSystem fileSystem;
    private final Map<String, String> nameToIdentifierMap = new ConcurrentHashMap<>();
    private final com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    @Inject
    public FileSystemAgentAdapter(Translator<Agent, String> translator,
                                  @Localic FileSystem fileSystem) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.translator = translator;
        this.fileSystem = fileSystem;
    }

    private String getBaseDir() {
        return this.configuration.get("anticorruption.agents.source");
    }

    @Override
    public List<Agent> toInternal() {
        String baseDir = this.getBaseDir();
        if (!this.fileSystem.exists(baseDir)) {
            return List.of();
        }
        return this.fileSystem.walk(baseDir)
                .map(this::extractIdFromPath)
                .map(this::toInternal)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Agent toInternal(String name) {
        String baseDir = this.getBaseDir();
        String path = Paths.get(baseDir, Util.toSnakeCase(name) + ".json").toString();
        return this.translator.toInternal(name, this.fileSystem.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public void toExternal(String name, Agent agent) {
        String baseDir = this.getBaseDir();
        String path = Paths.get(baseDir, Util.toSnakeCase(name) + ".json").toString();
        this.fileSystem.write(path, this.gson.toJson(this.flatten(agent)), StandardCharsets.UTF_8);
    }

    @Override
    public void toExternal(String name, String json) {
        String baseDir = this.getBaseDir();
        String path = Paths.get(baseDir, Util.toSnakeCase(name) + ".json").toString();
        this.fileSystem.write(path, json, StandardCharsets.UTF_8);
    }

    private Map<String, Object> flatten(Agent agent) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (agent == null) {
            return map;
        }
        try {
            Method[] methods = Agent.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() == 0 && !method.getReturnType().equals(Void.TYPE)) {
                    Object value = method.invoke(agent);
                    if (value instanceof List<?> list) {
                        map.put(method.getName(), list.stream()
                                .map(x -> (x instanceof Topic t) ? t.name() : x)
                                .collect(Collectors.toList()));
                    } else if (value instanceof Topic topic) {
                        map.put(method.getName(), topic.name());
                    } else {
                        map.put(method.getName(), value);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Flattening failed for agent: " + agent.name(), e);
        }
        return map;
    }

    private String extractIdFromPath(String path) {
        String baseDir = this.getBaseDir();
        return path.replace(baseDir, "")
                .replaceFirst("^[\\\\/]", "")
                .replaceFirst("\\.json$", "");
    }
}
