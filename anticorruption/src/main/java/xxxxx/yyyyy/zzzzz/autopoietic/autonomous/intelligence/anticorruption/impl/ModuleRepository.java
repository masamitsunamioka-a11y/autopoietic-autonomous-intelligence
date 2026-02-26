package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;

import java.util.List;

@ApplicationScoped
public class ModuleRepository implements Repository<Module> {
    private static final Logger logger = LoggerFactory.getLogger(ModuleRepository.class);
    private final Adapter<Module, String> adapter;
    private final JsonCodec jsonCodec;

    @Inject
    public ModuleRepository(Adapter<Module, String> adapter,
                            JsonCodec jsonCodec) {
        this.adapter = adapter;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Module find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Module> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(Engram engram) {
        this.adapter.publish(
            engram.name(), engram.transcribe(this.jsonCodec::marshal));
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
