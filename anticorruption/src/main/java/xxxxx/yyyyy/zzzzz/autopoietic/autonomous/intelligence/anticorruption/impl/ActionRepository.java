package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.util.List;

@ApplicationScoped
public class ActionRepository implements Repository<Action> {
    private static final Logger logger = LoggerFactory.getLogger(ActionRepository.class);
    private final Adapter<Action, String> adapter;

    @Inject
    public ActionRepository(Adapter<Action, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Action find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Action> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(String id, String json) {
        this.adapter.publish(id, json);
    }

    @Override
    public void store(String id, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException();
    }
}
