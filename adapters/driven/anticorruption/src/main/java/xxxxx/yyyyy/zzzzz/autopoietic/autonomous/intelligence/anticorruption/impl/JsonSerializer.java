package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;

import java.lang.reflect.Type;

@ApplicationScoped
public class JsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public <T> T deserialize(String string, Type type) {
        try {
            return this.objectMapper.readValue(this.clean(string), this.objectMapper.constructType(type));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String clean(String string) {
        if (string == null || string.isBlank()) {
            return "{}";
        }
        var as = string.indexOf('[');
        var os = string.indexOf('{');
        var isArray = as != -1 && (os == -1 || as < os);
        var o = isArray ? '[' : '{';
        var c = isArray ? ']' : '}';
        var s = isArray ? as : os;
        var e = string.lastIndexOf(c);
        return (s == -1 || e == -1 || s >= e)
            ? "" + o + c
            : string.substring(s, e + 1).trim();
    }
}
