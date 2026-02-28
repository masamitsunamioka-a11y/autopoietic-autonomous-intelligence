package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class FileSystemAreaAdapter implements Adapter<Area, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemAreaAdapter.class);
    private final Translator<Area, String> translator;
    private final FileSystem fileSystem;
    private final Path areasSource;

    @Inject
    public FileSystemAreaAdapter(Translator<Area, String> translator,
                                 @Localic FileSystem fileSystem) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        var configuration = new Configuration("anticorruption.yaml");
        this.areasSource = Path.of(configuration.get("anticorruption.areas.source"), "");
    }

    @Override
    public Area fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.fileSystem.read(
                this.areasSource.resolve(Util.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8));
    }

    @Override
    public List<Area> fetchAll() {
        return this.fileSystem.walk(this.areasSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.fileSystem.write(
            this.areasSource.resolve(Util.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
            this.areasSource.resolve(Util.toSnakeCase(id) + ".json"));
    }
}
