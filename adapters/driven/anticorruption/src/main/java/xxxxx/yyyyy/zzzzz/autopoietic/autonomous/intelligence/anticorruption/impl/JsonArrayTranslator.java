package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.lang.reflect.Array;
import java.util.List;

public class JsonArrayTranslator<I extends Entity, E extends Resource> implements Translator<List<I>, E> {
    private static final Logger logger = LoggerFactory.getLogger(JsonArrayTranslator.class);
    private final Serializer serializer;
    private final Class<I> type;

    public JsonArrayTranslator(Serializer serializer, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<I> internalize(E resource) {
        return List.of((I[]) this.serializer.deserialize(
            resource.data(), Array.newInstance(this.type, 0).getClass()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public E externalize(List<I> objects) {
        return (E) new FileResource(this.serializer.serialize(objects));
    }
}
