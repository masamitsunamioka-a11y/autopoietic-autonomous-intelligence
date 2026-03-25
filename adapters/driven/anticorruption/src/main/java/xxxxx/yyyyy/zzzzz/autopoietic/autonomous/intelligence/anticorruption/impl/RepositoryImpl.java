package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.util.List;
import java.util.Objects;

public class RepositoryImpl<T extends AggregateRoot> implements Repository<T> {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryImpl.class);
    private final Adapter<T> adapter;

    public RepositoryImpl(Adapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<T> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(T object) {
        this.adapter.publish(object);
    }

    @Override
    public void storeAll(List<? extends T> objects) {
        objects.forEach(this::store);
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }

    @Override
    public void removeAll(List<String> ids) {
        this.adapter.revokeAll(ids);
    }

    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }
}
