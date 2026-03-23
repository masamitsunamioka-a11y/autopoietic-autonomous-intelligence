package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JavaResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class JavaAdapter<I extends Entity> implements Adapter<I> {
    private static final Logger logger = LoggerFactory.getLogger(JavaAdapter.class);
    private final Extern extern;
    private final Translator<I, JavaResource> translator;
    private final String package_;

    public JavaAdapter(Translator<I, JavaResource> translator, Configuration configuration) {
        this.translator = translator;
        this.package_ = configuration.get("package");
        this.extern = new LocalFileSystem(Path.of(configuration.get("source"), ""));
    }

    @Override
    public I fetch(String id) {
        var resource = (JavaResource) this.extern.get(this.extern.resolve(this.toPath(id)));
        return resource != null ? this.translator.internalize(resource) : null;
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
        this.extern.put(new JavaResource(this.extern.resolve(this.toPath(object.id())), resource.content()));
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
        return Path.of((this.package_ + "." + id).replace(".", "/") + ".java");
    }
}
