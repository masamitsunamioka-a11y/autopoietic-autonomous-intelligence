package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.List;

public interface EventStore {
    <T extends Event> void save(List<T> events);

    List<Event> eventsForAggregate(String id);

    List<Event> allEvents();
}
