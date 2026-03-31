package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.List;

public interface EventStore {
    void save(String aggregateId, List<? extends Event> events, int expectedVersion);

    List<Event> eventsForAggregate(String aggregateId);
}
