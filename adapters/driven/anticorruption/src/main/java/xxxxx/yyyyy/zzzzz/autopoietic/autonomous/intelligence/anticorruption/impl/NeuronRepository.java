package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class NeuronRepository implements Repository<Neuron, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronRepository.class);
    private final Adapter<Neuron, Engravable> adapter;

    @Inject
    public NeuronRepository(Adapter<Neuron, Engravable> adapter) {
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
    public void store(Engravable engravable) {
        this.adapter.publish(engravable.name(), engravable);
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }

    @Override
    public void removeAll(List<String> ids) {
        ids.forEach(this.adapter::revoke);
    }

    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }
}
