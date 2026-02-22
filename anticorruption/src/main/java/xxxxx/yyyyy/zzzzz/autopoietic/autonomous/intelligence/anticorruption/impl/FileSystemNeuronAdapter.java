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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class FileSystemNeuronAdapter implements Adapter<Neuron, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemNeuronAdapter.class);
    private final Translator<Neuron, String> translator;
    private final FileSystem fileSystem;
    private final Path neuronsSource;

    @Inject
    public FileSystemNeuronAdapter(Translator<Neuron, String> translator,
                                   @Localic FileSystem fileSystem) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        var configuration = new Configuration("anticorruption.yaml");
        this.neuronsSource = Path.of(configuration.get("anticorruption.neurons.source"), "");
    }

    @Override
    public Neuron fetch(String id) {
        return this.translator.translateFrom(
            id,
            this.fileSystem.read(
                this.neuronsSource.resolve(Util.toSnakeCase(id) + ".json"),
                StandardCharsets.UTF_8));
    }

    @Override
    public List<Neuron> fetchAll() {
        return this.fileSystem.walk(this.neuronsSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.fileSystem.write(
            this.neuronsSource.resolve(Util.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.fileSystem.delete(
            this.neuronsSource.resolve(Util.toSnakeCase(id) + ".json"));
    }
}
