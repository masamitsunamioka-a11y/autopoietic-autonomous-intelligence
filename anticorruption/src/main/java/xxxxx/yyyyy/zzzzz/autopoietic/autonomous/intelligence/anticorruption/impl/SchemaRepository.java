package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Schema;

import java.util.List;

@ApplicationScoped
public class SchemaRepository implements Repository<Schema> {
    private static final Logger logger = LoggerFactory.getLogger(SchemaRepository.class);
    private final Adapter<Schema, String> adapter;
    private final JsonCodec jsonCodec;

    @Inject
    public SchemaRepository(Adapter<Schema, String> adapter,
                            JsonCodec jsonCodec) {
        this.adapter = adapter;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Schema find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Schema> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(Engram engram) {
        this.adapter.publish(engram.name(), engram.transcribe(this.jsonCodec::marshal));
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }
}
