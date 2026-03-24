package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JavaResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.MarkdownResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class AdapterImpl<I extends Entity, E extends Resource> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(AdapterImpl.class);
    private final Extern extern;
    private final Translator<I, E> translator;
    private final AdapterPlugin plugin;

    public AdapterImpl(Extern extern, Translator<I, E> translator, AdapterPlugin plugin) {
        this.extern = extern;
        this.translator = translator;
        this.plugin = plugin;
    }

    @Override
    public I fetch(String id) {
        var name = this.plugin.onNaming(id);
        var resource = (E) this.extern.get(this.extern.resolve(name));
        return resource != null ? this.translator.internalize(resource) : null;
    }

    @Override
    public List<I> fetchAll() {
        return this.extern.set().stream()
            .sorted(Comparator.comparing(URI::toString))
            .map(x -> (E) this.extern.get(x))
            .filter(Objects::nonNull)
            .map(this.translator::internalize)
            .toList();
    }

    @Override
    public void publish(I object) {
        var resource = this.translator.externalize(object);
        var uri = this.extern.resolve(this.plugin.onNaming(object.id()));
        this.extern.put(this.locate(resource, uri));
    }

    private E locate(E resource, java.net.URI uri) {
        return (E) switch (resource) {
            case JsonResource r -> new JsonResource(uri, r.content());
            case JavaResource r -> new JavaResource(uri, r.content());
            case MarkdownResource r -> new MarkdownResource(uri, r.content());
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void revoke(String id) {
        var name = this.plugin.onNaming(id);
        this.extern.remove(this.extern.resolve(name));
    }

    @Override
    public void revokeAll(List<String> ids) {
        ids.forEach(this::revoke);
    }
}
