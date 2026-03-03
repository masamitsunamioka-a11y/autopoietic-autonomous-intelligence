package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EffectorAdapter implements Adapter<Effector, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorAdapter.class);
    private final Translator<Effector, Engravable> translator;
    private final Storage storage;
    private final String package_;
    private final Path source;

    @Inject
    public EffectorAdapter(Translator<Effector, Engravable> translator,
                           Storage storage) {
        this.translator = translator;
        this.storage = storage;
        var configuration = new Configuration().anticorruption();
        this.package_ = configuration.get("neural.effectors.package");
        this.source = Path.of(configuration.get("neural.effectors.source"), "");
    }

    @Override
    public Effector fetch(String id) {
        var javaFile = this.source.resolve(
            Path.of(this.package_.replace(".", "/"), id + ".java"));
        if (!this.storage.exists(javaFile)) {
            return null;
        }
        return this.translator.translateFrom(id, id);
    }

    @Override
    public List<Effector> fetchAll() {
        var packageDir = this.source.resolve(
            Path.of(this.package_.replace(".", "/"), ""));
        return this.storage.list(packageDir)
            .filter(x -> x.toString().endsWith(".java"))
            .map(x -> x.getFileName().toString().replace(".java", ""))
            .map(this::fetch)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public void publish(String id, Engravable engravable) {
        this.storage.write(
            this.source.resolve(
                Path.of(this.package_.replace(".", "/"), id + ".java")),
            this.translator.translateTo(id, engravable),
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        throw new UnsupportedOperationException();
    }
}
