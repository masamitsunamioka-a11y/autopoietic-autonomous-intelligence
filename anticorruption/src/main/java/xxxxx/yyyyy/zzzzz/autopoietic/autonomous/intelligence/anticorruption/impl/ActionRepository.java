package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.util.List;

@ApplicationScoped
public class ActionRepository implements Repository<Action<?>> {
    private final Adapter<Action<?>, String> adapter;

    @Inject
    public ActionRepository(Adapter<Action<?>, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public List<Action<?>> findAll() {
        return this.adapter.toInternal();
    }

    @Override
    public Action<?> findByName(String name) {
        return this.adapter.toInternal(name);
    }

    @Override
    public void store(Action<?> action) {
        this.store(action.getClass().getSimpleName(), action);
    }

    @Override
    public void store(String name, Action<?> action) {
        this.adapter.toExternal(name, action);
    }

    @Override
    public void store(String name, String code) {
        this.adapter.toExternal(name, code);
    }
}
