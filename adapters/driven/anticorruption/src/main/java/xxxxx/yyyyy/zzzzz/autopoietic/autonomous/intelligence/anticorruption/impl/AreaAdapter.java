package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Utility;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class AreaAdapter implements Adapter<Area, String> {
    private static final Logger logger = LoggerFactory.getLogger(AreaAdapter.class);
    private final Translator<Area, String> translator;
    private final Storage storage;
    private final Path areasSource;

    @Inject
    public AreaAdapter(Translator<Area, String> translator,
                       Storage storage) {
        this.translator = translator;
        this.storage = storage;
        var configuration = new Configuration();
        this.areasSource = Path.of(configuration.get("anticorruption.areas.source"), "");
    }

    @Override
    public Area fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.storage.read(
                this.areasSource.resolve(Utility.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8));
    }

    @Override
    public List<Area> fetchAll() {
        return this.storage.walk(this.areasSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.storage.write(
            this.areasSource.resolve(Utility.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.storage.delete(
            this.areasSource.resolve(Utility.toSnakeCase(id) + ".json"));
    }
}
