package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;

import java.util.List;

@ApplicationScoped
public class EffectorRepository implements Repository<Effector> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorRepository.class);
    private final Adapter<Effector, String> adapter;
    private final JsonCodec jsonCodec;

    @Inject
    public EffectorRepository(Adapter<Effector, String> adapter,
                              JsonCodec jsonCodec) {
        this.adapter = adapter;
        this.jsonCodec = jsonCodec;
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
    public void store(Engram engram) {
        this.adapter.publish(
            engram.name(), engram.transcribe(this.jsonCodec::marshal));
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException();
    }
}
