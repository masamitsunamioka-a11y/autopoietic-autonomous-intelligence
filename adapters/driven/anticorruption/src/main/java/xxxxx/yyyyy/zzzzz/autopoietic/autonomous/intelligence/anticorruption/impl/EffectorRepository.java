package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EffectorRepository implements Repository<Effector, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorRepository.class);
    private final Adapter<Effector, Engravable> adapter;

    @Inject
    public EffectorRepository(Adapter<Effector, Engravable> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Effector find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Effector> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(Engravable engravable) {
        this.adapter.publish(engravable.name(), engravable);
    }

    /// @formatter:off
    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeAll(List<String> ids) {
        throw new UnsupportedOperationException();
    }
    /// @formatter:on
    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }
}
