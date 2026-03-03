package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class AreaRepository implements Repository<Area, Engravable> {
    private static final Logger logger = LoggerFactory.getLogger(AreaRepository.class);
    private final Adapter<Area, Engravable> adapter;

    @Inject
    public AreaRepository(Adapter<Area, Engravable> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Area find(String id) {
        return this.adapter.fetch(id);
    }

    @Override
    public List<Area> findAll() {
        return this.adapter.fetchAll();
    }

    @Override
    public void store(Engravable engravable) {
        this.adapter.publish(engravable.name(), engravable);
    }

    @Override
    public void remove(String id) {
        this.adapter.revoke(id);
    }

    @Override
    public void removeAll(List<String> ids) {
        ids.forEach(this.adapter::revoke);
    }

    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }
}
