package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class AreaAdapter implements Adapter<Area, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(AreaAdapter.class);
    private final Translator<Area, Engravable> translator;
    private final Storage storage;
    private final Path target;

    @Inject
    public AreaAdapter(Translator<Area, Engravable> translator,
                       Storage storage) {
        this.translator = translator;
        this.storage = storage;
        var configuration = new Configuration().anticorruption();
        this.target = Path.of(configuration.get("neural.areas.target"), "");
    }

    @Override
    public Area fetch(String id) {
        var path = this.target.resolve(Utility.toSnakeCase(id) + ".json");
        if (!this.storage.exists(path)) {
            return null;
        }
        return this.translator.translateFrom(
            id, this.storage.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public List<Area> fetchAll() {
        return this.storage.walk(this.target)
            .map(x -> x.getFileName().toString().replace(".json", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, Engravable engravable) {
        this.storage.write(
            this.target.resolve(Utility.toSnakeCase(id) + ".json"),
            this.translator.translateTo(id, engravable),
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.storage.delete(
            this.target.resolve(Utility.toSnakeCase(id) + ".json"));
    }
}
