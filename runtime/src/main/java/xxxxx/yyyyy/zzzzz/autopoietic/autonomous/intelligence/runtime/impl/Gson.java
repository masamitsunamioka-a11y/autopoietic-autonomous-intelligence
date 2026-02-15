package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.JsonParser;

import java.lang.reflect.Type;

@ApplicationScoped
public class Gson implements JsonParser {
    private static final Logger logger = LoggerFactory.getLogger(Gson.class);
    private final com.google.gson.Gson gson;

    public Gson() {
        this.gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public <T> T from(String json, Type type) {
        return this.gson.fromJson(this.clean(json), type);
    }

    @Override
    public String to(Object object) {
        return this.gson.toJson(object);
    }

    private String clean(String json) {
        if (json == null || json.isBlank()) {
            return "{}";
        }
        int start = json.indexOf("{");
        int end = json.lastIndexOf("}");
        if (start == -1 || end == -1 || start >= end) {
            return "{}";
        }
        return json.substring(start, end + 1).trim();
    }
}
