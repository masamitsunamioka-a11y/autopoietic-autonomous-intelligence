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

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.toFqcn;

public class JavaAdapter<I extends Entity> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(JavaAdapter.class);
    private final Extern extern;
    private final Translator<I, Resource> translator;
    private final String package_;

    public JavaAdapter(Translator<I, Resource> translator, Class<I> type) {
        this.translator = translator;
        var configuration = new Configuration().neural(type);
        this.package_ = configuration.get("package");
        this.extern = new LocalFileSystem(Path.of(configuration.get("source"), ""));
    }

    @Override
    public I fetch(String id) {
        var uri = this.extern.resolve(this.toPath(id));
        var resource = this.extern.get(uri);
        return resource != null
            ? this.translator.internalize(resource)
            : null;
    }

    @Override
    public List<I> fetchAll() {
        return this.extern.set().stream()
            .filter(x -> x.getPath().endsWith(".java"))
            .map(x -> Path.of(x).getFileName().toString().replace(".java", ""))
            .map(this::fetch)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public void publish(I object) {
        var resource = this.translator.externalize(object);
        var uri = this.extern.resolve(this.toPath(object.id()));
        this.extern.put(uri, resource);
    }

    @Override
    public void revoke(String id) {
        this.extern.remove(this.extern.resolve(this.toPath(id)));
    }

    @Override
    public void revokeAll(List<String> ids) {
        ids.forEach(this::revoke);
    }

    private Path toPath(String id) {
        return Path.of(toFqcn(this.package_, id).replace(".", "/") + ".java");
    }
}
