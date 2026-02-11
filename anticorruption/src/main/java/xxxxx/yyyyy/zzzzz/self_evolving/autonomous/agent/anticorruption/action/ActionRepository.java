package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;

import java.util.List;

@ApplicationScoped
public class ActionRepository implements Repository<Action<?>> {
    private final Adapter<Action<?>, String> actionAdapter;
    private final Translator<Action<?>, String> translator;

    @Inject
    public ActionRepository(Adapter<Action<?>, String> adapter,
                            Translator<Action<?>, String> translator) {
        this.actionAdapter = adapter;
        this.translator = translator;
    }

    @Override
    public List<Action<?>> findAll() {
        return this.actionAdapter.toInternal();
    }

    @Override
    public Action<?> findByName(String name) {
        return this.actionAdapter.toInternal(name);
    }

    @Override
    public void store(Action<?> action) {
        this.store(action.getClass().getSimpleName(), action);
    }

    @Override
    public void store(String name, Action<?> action) {
        this.actionAdapter.toExternal(name, action);
    }

    @Override
    public void store(String name, String code) {
        this.actionAdapter.toExternal(name, code);
    }
}
