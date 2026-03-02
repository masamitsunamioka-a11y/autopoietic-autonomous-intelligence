package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;

@ApplicationScoped
public class NeuronRepository implements Repository<Neuron, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronRepository.class);
    private final Adapter<Neuron, String> adapter;
    private final Serializer serializer;

    @Inject
    public NeuronRepository(Adapter<Neuron, String> adapter, Serializer serializer) {
        this.adapter = adapter;
        this.serializer = serializer;
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
    public void store(Engravable engravable) {
        this.adapter.publish(
            engravable.name(), this.serializer.serialize(engravable));
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
