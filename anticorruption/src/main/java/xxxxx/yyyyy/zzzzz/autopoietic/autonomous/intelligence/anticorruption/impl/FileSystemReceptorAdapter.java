package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Receptor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class FileSystemReceptorAdapter implements Adapter<Receptor, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemReceptorAdapter.class);
    private final Translator<Receptor, String> translator;
    private final FileSystem fileSystem;
    private final Path receptorsSource;

    @Inject
    public FileSystemReceptorAdapter(Translator<Receptor, String> translator,
                                     @Localic FileSystem fileSystem) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        var configuration = new Configuration("anticorruption.yaml");
        this.receptorsSource = Path.of(configuration.get("anticorruption.receptors.source"), "");
    }

    @Override
    public Receptor fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.fileSystem.read(
                this.receptorsSource.resolve(Util.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8));
    }

    @Override
    public List<Receptor> fetchAll() {
        return this.fileSystem.walk(this.receptorsSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.fileSystem.write(
            this.receptorsSource.resolve(Util.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
            this.receptorsSource.resolve(Util.toSnakeCase(id) + ".json"));
    }
}
