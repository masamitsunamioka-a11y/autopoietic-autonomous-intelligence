package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JavaResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.MarkdownResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class AdapterImpl<I extends Entity, R, E extends Resource> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(AdapterImpl.class);
    private final Extern extern;
    private final Translator<R, E> translator;
    private final Function<String, String> namer;
    private final Predicate<R> filter;
    private final Function<R, I> extractor;

    public AdapterImpl(Extern extern, Translator<R, E> translator,
                       Function<String, String> namer) {
        this(extern, translator, namer, x -> true, x -> (I) x);
    }

    public AdapterImpl(Extern extern, Translator<R, E> translator,
                       Function<String, String> namer,
                       Predicate<R> filter, Function<R, I> extractor) {
        this.extern = extern;
        this.translator = translator;
        this.namer = namer;
        this.filter = filter;
        this.extractor = extractor;
    }

    @Override
    public I fetch(String id) {
        var uri = this.extern.resolve(this.namer.apply(id));
        return this.extractor.apply(this.translator.internalize((E) this.extern.get(uri)));
    }

    @Override
    public List<I> fetchAll() {
        return this.extern.set().stream()
            .sorted(Comparator.comparing(URI::toString))
            .map(x -> (E) this.extern.get(x))
            .filter(Objects::nonNull)
            .map(this.translator::internalize)
            .filter(this.filter)
            .map(this.extractor)
            .toList();
    }

    @Override
    public void publish(I object) {
        var resource = this.translator.externalize((R) object);
        var uri = this.extern.resolve(this.namer.apply(object.id()));
        this.extern.put(this.locate(resource, uri));
    }

    @Override
    public void revoke(String id) {
        var uri = this.extern.resolve(this.namer.apply(id));
        this.extern.remove(uri);
    }

    @Override
    public void revokeAll(List<String> ids) {
        ids.forEach(this::revoke);
    }

    private E locate(E resource, URI uri) {
        return (E) switch (resource) {
            case JsonResource x -> new JsonResource(uri, x.content());
            case JavaResource x -> new JavaResource(uri, x.content());
            case MarkdownResource x -> new MarkdownResource(uri, x.content());
            default -> throw new IllegalArgumentException();
        };
    }
}
