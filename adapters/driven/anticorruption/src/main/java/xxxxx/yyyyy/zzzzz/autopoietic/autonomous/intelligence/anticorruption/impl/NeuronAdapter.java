package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Utility;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class NeuronAdapter implements Adapter<Neuron, String> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronAdapter.class);
    private final Translator<Neuron, String> translator;
    private final Storage storage;
    private final Path neuronsSource;

    @Inject
    public NeuronAdapter(Translator<Neuron, String> translator,
                         Storage storage) {
        this.translator = translator;
        this.storage = storage;
        var configuration = new Configuration();
        this.neuronsSource = Path.of(configuration.get("anticorruption.neurons.source"), "");
    }

    @Override
    public Neuron fetch(String id) {
        var path = this.neuronsSource.resolve(Utility.toSnakeCase(id) + ".json");
        if (!this.storage.exists(path)) {
            return null;
        }
        return this.translator.translateFrom(
            id, this.storage.read(path, StandardCharsets.UTF_8));
    }

    @Override
    public List<Neuron> fetchAll() {
        return this.storage.walk(this.neuronsSource)
            .map(x -> x.replaceAll(".*/|\\.json$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        this.storage.write(
            this.neuronsSource.resolve(Utility.toSnakeCase(id) + ".json"),
            json,
            StandardCharsets.UTF_8);
    }

    @Override
    public void revoke(String id) {
        this.storage.delete(
            this.neuronsSource.resolve(Utility.toSnakeCase(id) + ".json"));
    }
}
