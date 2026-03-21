package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.lang.reflect.Type;

@ApplicationScoped
public class JsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private final Gson gson;

    public JsonSerializer() {
        this.gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            ///.setPrettyPrinting()
            .registerTypeAdapter(Trace.class,
                (JsonDeserializer<Trace>) (json, type, context) -> {
                    var obj = json.getAsJsonObject();
                    var id = obj.get("id").getAsString();
                    var content = context.deserialize(
                        obj.get("content"), Object.class);
                    return new TraceImpl(id, content);
                })
            .create();
    }

    @Override
    public <T> T deserialize(String string, Type type) {
        return this.gson.fromJson(this.clean(string), type);
    }

    @Override
    public String serialize(Object object) {
        return this.gson.toJson(object);
    }

    private String clean(String data) {
        if (data == null || data.isBlank()) {
            return "{}";
        }
        var trimmed = data.trim();
        if (trimmed.startsWith("[")) {
            var start = data.indexOf("[");
            var end = data.lastIndexOf("]");
            if (start == -1 || end == -1 || start >= end) {
                return "[]";
            }
            return data.substring(start, end + 1).trim();
        }
        var start = data.indexOf("{");
        var end = data.lastIndexOf("}");
        if (start == -1 || end == -1 || start >= end) {
            return "{}";
        }
        return data.substring(start, end + 1).trim();
    }
}
