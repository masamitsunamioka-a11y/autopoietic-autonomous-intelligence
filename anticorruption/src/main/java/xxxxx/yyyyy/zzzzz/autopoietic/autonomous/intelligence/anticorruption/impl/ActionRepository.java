package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.util.List;

@ApplicationScoped
public class ActionRepository implements Repository<Action> {
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
    public void store(String id, String source) {
        this.adapter.publish(id, source);
    }
}
