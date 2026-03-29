package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.io.UncheckedIOException;
import java.util.List;

public class RepositoryImpl<T extends AggregateRoot> implements Repository<T> {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryImpl.class);
    private final Adapter<T> adapter;

    public RepositoryImpl(Adapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T find(String id) {
        try {
            return this.adapter.fetch(id);
        } catch (UncheckedIOException e) {
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public boolean exists(String id) {
        return this.find(id) != null;
    }
}
