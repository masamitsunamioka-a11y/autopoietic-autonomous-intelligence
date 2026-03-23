package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.lang.reflect.Proxy;
import java.util.Map;

public class JsonTranslator<I extends Entity> implements Translator<I, JsonResource> {
    private static final Logger logger = LoggerFactory.getLogger(JsonTranslator.class);
    private final Serializer serializer;
    private final Class<I> type;

    public JsonTranslator(Serializer serializer, Configuration configuration, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I internalize(JsonResource resource) {
        Map<String, Object> map = this.serializer.deserialize(resource.content(), Map.class);
        return (I) Proxy.newProxyInstance(
            this.type.getClassLoader(),
            new Class<?>[]{this.type, JsonSerializable.class},
            (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "serialize", "serializeWithType" -> {
                        ((JsonGenerator) args[0]).writeObject(map);
                        yield null;
                    }
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> args != null && args.length == 1 && proxy == args[0];
                    default -> map.get(method.getName());
                };
            }
        );
    }

    @Override
    public JsonResource externalize(I object) {
        return new JsonResource(null, this.serializer.serialize(object));
    }
}
