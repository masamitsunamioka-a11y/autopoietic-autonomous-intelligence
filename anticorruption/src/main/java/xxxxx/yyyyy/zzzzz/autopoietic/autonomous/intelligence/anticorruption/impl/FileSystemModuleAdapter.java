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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class FileSystemModuleAdapter implements Adapter<Module, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemModuleAdapter.class);
    private final Translator<Module, String> translator;
    private final FileSystem fileSystem;
    private final Path modulesSource;

    @Inject
    public FileSystemModuleAdapter(Translator<Module, String> translator,
                                   @Localic FileSystem fileSystem) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        var configuration = new Configuration("anticorruption.yaml");
        this.modulesSource = Path.of(configuration.get("anticorruption.modules.source"), "");
    }

    @Override
    public Module fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.fileSystem.read(
                this.modulesSource.resolve(Util.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8));
    }

    @Override
    public List<Module> fetchAll() {
        return this.fileSystem.walk(this.modulesSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.fileSystem.write(
            this.modulesSource.resolve(Util.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
            this.modulesSource.resolve(Util.toSnakeCase(id) + ".json"));
    }
}
