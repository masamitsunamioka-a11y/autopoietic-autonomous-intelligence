package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;

@ApplicationScoped
public class NeuronRepository implements Repository<Neuron, Engram> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronRepository.class);
    private final Adapter<Neuron, String> adapter;
    private final JsonCodec jsonCodec;

    @Inject
    public NeuronRepository(Adapter<Neuron, String> adapter, JsonCodec jsonCodec) {
        this.adapter = adapter;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Neuron find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Neuron> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(Engram engram) {
        this.adapter.publish(
            engram.name(), engram.encode(this.jsonCodec::marshal));
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
