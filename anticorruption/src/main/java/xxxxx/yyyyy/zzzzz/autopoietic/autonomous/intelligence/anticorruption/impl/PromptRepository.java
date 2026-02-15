package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;

@ApplicationScoped
public class PromptRepository implements Repository<String> {
    private final Adapter<String, String> adapter;

    @Inject
    public PromptRepository(Adapter<String, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public String find(String id) {
        return this.adapter.fetch(id);
    }
}
