package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Integer.toHexString;
import static java.lang.System.identityHashCode;

public class PureJavaContextApplicationScoped extends PureJavaContext {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaContextApplicationScoped.class);
    private final Map<Type, Map<Set<? extends Annotation>, Object>> instances;

    public PureJavaContextApplicationScoped() {
        this.instances = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Contextual<T> contextual) {
        return (T) this.instances.computeIfAbsent(
                this.reveal(contextual).type(),
                x -> new ConcurrentHashMap<>())
            .computeIfAbsent(
                this.reveal(contextual).qualifiers(),
                x -> contextual.create(null));
    }

    @Override
    public Class<? extends Annotation> scope() {
        return ApplicationScoped.class;
    }

    private <T> PureJavaContextual<T> reveal(Contextual<T> contextual) {
        return (PureJavaContextual<T>) contextual;
    }

    @Override
    public String toString() {
        try {
            Map<String, String> instanceIdMap =
                this.instances.entrySet().stream()
                    .flatMap(x -> x.getValue().entrySet().stream()
                        .map(y -> Map.entry(
                            x.getKey().getTypeName() + "[" +
                                y.getKey().stream()
                                    .map(z -> z.annotationType().getSimpleName())
                                    .sorted()
                                    .collect(Collectors.joining(", ")) + "]",
                            toHexString(identityHashCode(y.getValue()))
                        )))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (k, v) -> k,
                        LinkedHashMap::new));
            JsonObject root = new JsonObject();
            root.add(this.scope().toString(), new Gson().toJsonTree(instanceIdMap));
            return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(root);
        } catch (Exception e) {
            return """
                {"instanceMap_error": "%s"}
                """.formatted(e.getMessage());
        }
    }
}
