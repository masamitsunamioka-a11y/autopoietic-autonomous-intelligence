package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Storable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;

import java.util.List;

@ApplicationScoped
public class NeuronRepository implements Repository<Neuron> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronRepository.class);
    private final Adapter<Neuron, String> adapter;

    @Inject
    public NeuronRepository(Adapter<Neuron, String> adapter) {
        this.adapter = adapter;
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
    public void store(String id, Storable storable) {
        this.adapter.publish(id, storable.serialize());
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
