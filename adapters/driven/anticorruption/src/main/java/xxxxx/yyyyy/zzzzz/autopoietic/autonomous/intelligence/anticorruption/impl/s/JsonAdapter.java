package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.AdapterPlugin;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class JsonAdapter<I extends Entity> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(JsonAdapter.class);
    private final Extern extern;
    private final Translator<I, JsonResource> translator;
    private final List<AdapterPlugin<I>> pluginChain;

    public JsonAdapter(Translator<I, JsonResource> translator,
                       Configuration configuration,
                       List<AdapterPlugin<I>> pluginChain) {
        this.translator = translator;
        this.extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        this.pluginChain = pluginChain;
    }

    @Override
    public I fetch(String id) {
        return this.files().stream()
            .map(x -> (JsonResource) this.extern.get(x))
            .map(this.translator::internalize)
            .map(this::applyOnFetch)
            .filter(x -> x.id().contains(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<I> fetchAll() {
        return this.files().stream()
            .map(x -> (JsonResource) this.extern.get(x))
            .map(this.translator::internalize)
            .map(this::applyOnFetch)
            .toList();
    }

    @Override
    public void publish(I object) {
        var applied = this.applyOnPublish(object);
        var resource = this.translator.externalize(applied);
        var name = this.toSnakeCase(applied.id()) + ".json";
        this.extern.put(new JsonResource(this.extern.resolve(name), resource.content()));
    }

    @Override
    public void revoke(String id) {
        var suffix = this.toSnakeCase(id) + ".json";
        this.files().stream()
            .filter(x -> x.getPath().endsWith(suffix))
            .forEach(this.extern::remove);
    }

    @Override
    public void revokeAll(List<String> ids) {
        ids.forEach(this::revoke);
    }

    private List<URI> files() {
        return this.extern.set().stream()
            .filter(x -> x.getPath().endsWith(".json"))
            .sorted(Comparator.comparing(URI::toString))
            .toList();
    }

    private I applyOnFetch(I entity) {
        return this.pluginChain.stream().reduce(entity, (x, y) -> y.onFetch(x), (x, y) -> y);
    }

    private I applyOnPublish(I entity) {
        return this.pluginChain.stream().reduce(entity, (x, y) -> y.onPublish(x), (x, y) -> y);
    }

    private String toSnakeCase(String s) {
        return s.replaceAll("([A-Z])", "_$1")
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_|_$", "");
    }
}
