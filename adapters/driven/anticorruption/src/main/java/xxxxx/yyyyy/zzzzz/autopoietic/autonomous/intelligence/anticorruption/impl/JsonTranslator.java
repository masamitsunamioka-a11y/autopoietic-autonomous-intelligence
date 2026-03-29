package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.lang.reflect.Proxy;
import java.util.Map;

public class JsonTranslator<I> implements Translator<I, JsonResource> {
    private static final Logger logger = LoggerFactory.getLogger(JsonTranslator.class);
    private final Serializer serializer;
    private final Class<I> type;

    public JsonTranslator(Serializer serializer, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I internalize(JsonResource resource) {
        var node = this.serializer.<JsonNode>deserialize(resource.content(), JsonNode.class);
        if (this.isEngram(node)) {
            return (I) new EngramImpl(node.get("strength").asDouble(), proxy(node.get("trace"), Trace.class));
        }
        return proxy(node, this.type);
    }

    @Override
    public JsonResource externalize(I object) {
        if (object instanceof Engram x) {
            return new JsonResource(null, this.serializer.serialize(Map.of("strength", x.strength(), "trace", Map.of("id", x.trace().id(), "content", x.trace().content()))));
        }
        return new JsonResource(null, this.serializer.serialize(object));
    }

    private boolean isEngram(JsonNode node) {
        return node.has("strength") && node.has("trace");
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(JsonNode node, Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(),
            new Class<?>[]{type, JsonSerializable.class},
            (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "serialize", "serializeWithType" -> {
                        ((JsonGenerator) args[0]).writeObject(node);
                        yield null;
                    }
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> args != null && args.length == 1 && proxy == args[0];
                    default -> {
                        var value = node.get(method.getName());
                        if (value == null || value.isNull()) yield null;
                        /// @formatter:off
                        if (value.isTextual())  yield value.asText();
                        if (value.isNumber())   yield value.numberValue();
                        if (value.isBoolean())  yield value.asBoolean();
                        if (value.isArray())    yield node.findValuesAsText(method.getName());
                        /// @formatter:on
                        yield value;
                    }
                };
            });
    }

    private record EngramImpl(double strength, Trace trace) implements Engram {
    }
}
