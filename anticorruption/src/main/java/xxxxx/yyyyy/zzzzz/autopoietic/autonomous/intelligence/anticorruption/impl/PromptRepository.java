package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;

import java.util.List;

@ApplicationScoped
public class PromptRepository implements Repository<String> {
    private final Adapter<String, String> adapter;

    @Inject
    public PromptRepository(Adapter<String, String> adapter) {
        this.adapter = adapter;
    }

    @Override
    public List<String> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findByName(String name) {
        return this.adapter.toInternal(name);
    }

    @Override
    public void store(String internal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void store(String filePath, String internal) {
        throw new UnsupportedOperationException();
    }
}
