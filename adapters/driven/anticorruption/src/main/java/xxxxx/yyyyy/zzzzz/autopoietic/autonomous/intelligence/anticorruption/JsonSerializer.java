package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;

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
            .create();
    }

    @Override
    public <T> T deserialize(String data, Type type) {
        return this.gson.fromJson(this.clean(data), type);
    }

    @Override
    public String serialize(Object object) {
        return this.gson.toJson(object);
    }

    private String clean(String data) {
        if (data == null || data.isBlank()) {
            return "{}";
        }
        int start = data.indexOf("{");
        int end = data.lastIndexOf("}");
        if (start == -1 || end == -1 || start >= end) {
            return "{}";
        }
        return data.substring(start, end + 1).trim();
    }
}
