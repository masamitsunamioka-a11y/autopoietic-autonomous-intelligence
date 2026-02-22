package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Storable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;

import java.util.List;

@ApplicationScoped
public class AgentRepository implements Repository<Agent> {
    private static final Logger logger = LoggerFactory.getLogger(AgentRepository.class);
    private final Adapter<Agent, String> adapter;

    @Inject
    public AgentRepository(Adapter<Agent, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Agent find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Agent> findAll() {
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
