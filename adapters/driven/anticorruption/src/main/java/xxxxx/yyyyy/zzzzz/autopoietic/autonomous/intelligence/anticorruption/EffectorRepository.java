package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;

import java.util.List;

@ApplicationScoped
public class EffectorRepository implements Repository<Effector, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorRepository.class);
    private final Adapter<Effector, String> adapter;
    private final Serializer serializer;

    @Inject
    public EffectorRepository(Adapter<Effector, String> adapter, Serializer serializer) {
        this.adapter = adapter;
        this.serializer = serializer;
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
        this.adapter.publish(
            engravable.name(), this.serializer.serialize(engravable));
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException();
    }
}
