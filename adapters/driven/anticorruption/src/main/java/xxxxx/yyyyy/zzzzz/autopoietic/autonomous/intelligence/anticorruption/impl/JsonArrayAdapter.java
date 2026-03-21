package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class JsonArrayAdapter<I extends Entity> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(JsonArrayAdapter.class);
    private final Extern extern;
    private final Translator<List<I>, Resource> translator;
    private final URI uri;
    private final long limit;

    public JsonArrayAdapter(Translator<List<I>, Resource> translator, Class<?> type) {
        this.translator = translator;
        var isKnowledge = type == Knowledge.class;
        var configuration = isKnowledge
            ? new Configuration().neocortical().knowledge()
            : new Configuration().hippocampal().episode();
        this.extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        this.limit = isKnowledge ? 1 : configuration.getLong("limit");
        this.uri = this.extern.resolve(this.toName(type));
    }

    @Override
    public I fetch(String id) {
        return this.fetchAll().stream()
            .filter(x -> x.id().contains(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<I> fetchAll() {
        return this.targetResources()
            .flatMap(this::getAndTranslate)
            .map(x -> x instanceof Trace t
                ? (I) new TraceImpl(this.prefixOf(t.id()), t.content())
                : x)
            .toList();
    }

    private String prefixOf(String id) {
        var at = id.lastIndexOf('@');
        return at >= 0 ? id.substring(0, at) : id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(I object) {
        var all = new ArrayList<>(this.fetchLatest());
        all.add(object instanceof Trace x
            ? (I) new TraceImpl(
            x.id() + "@" + Instant.now(), x.content())
            : object);
        this.translateAndPut(this.uri, all);
    }

    @Override
    public void revoke(String id) {
        this.translateAndPut(this.uri, this.fetchLatest().stream()
            .filter(x -> !x.id().contains(id))
            .toList());
    }

    @Override
    public void revokeAll(List<String> ids) {
        this.targetResources().forEach(x -> {
            this.translateAndPut(x, this.getAndTranslate(x)
                .filter(y -> !ids.contains(y.id()))
                .toList());
        });
    }

    private List<I> fetchLatest() {
        return this.extern.contains(this.uri)
            ? this.getAndTranslate(uri).toList()
            : List.of();
    }

    private Stream<I> getAndTranslate(URI uri) {
        var resource = this.extern.get(uri);
        return resource != null
            ? this.translator.internalize(resource).stream()
            : Stream.empty();
    }

    private void translateAndPut(URI uri, List<I> objects) {
        this.extern.put(uri, this.translator.externalize(objects));
    }

    private Stream<URI> targetResources() {
        return this.extern.set().stream()
            .filter(x -> x.getPath().endsWith(".json"))
            .sorted(Comparator.comparing(URI::toString).reversed())
            .limit(this.limit);
    }

    private String toName(Class<?> type) {
        var basename = type.getSimpleName().toLowerCase();
        return this.limit <= 1
            ? basename + ".json"
            : basename + "_" + now().format(ofPattern("yyyyMMddHHmmss")) + ".json";
    }
}
