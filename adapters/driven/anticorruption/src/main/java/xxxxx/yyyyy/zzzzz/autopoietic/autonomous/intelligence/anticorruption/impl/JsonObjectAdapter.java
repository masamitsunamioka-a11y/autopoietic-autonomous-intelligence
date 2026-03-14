package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.toSnakeCase;

public class JsonObjectAdapter<I extends Entity> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectAdapter.class);
    private final Extern extern;
    private final Translator<I, Resource> translator;

    public JsonObjectAdapter(Translator<I, Resource> translator, Class<I> type) {
        this.translator = translator;
        var configuration = new Configuration().neural(type);
        this.extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
    }

    @Override
    public I fetch(String id) {
        var uri = this.extern.resolve(this.toName(id));
        var resource = this.extern.get(uri);
        return resource != null
            ? this.translator.internalize(resource)
            : null;
    }

    @Override
    public List<I> fetchAll() {
        return this.extern.set().stream()
            .filter(x -> x.getPath().endsWith(".json"))
            .map(x -> Path.of(x).getFileName().toString().replace(".json", ""))
            .map(this::fetch)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public void publish(I object) {
        var resource = this.translator.externalize(object);
        var uri = this.extern.resolve(this.toName(object.id()));
        this.extern.put(uri, resource);
    }

    @Override
    public void revoke(String id) {
        this.extern.remove(this.extern.resolve(this.toName(id)));
    }

    @Override
    public void revokeAll(List<String> ids) {
        ids.forEach(this::revoke);
    }

    private String toName(String id) {
        return toSnakeCase(id) + ".json";
    }
}
