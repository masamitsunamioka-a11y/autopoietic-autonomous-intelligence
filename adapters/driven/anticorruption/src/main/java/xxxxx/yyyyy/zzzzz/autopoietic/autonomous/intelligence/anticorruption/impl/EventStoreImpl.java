package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventStore;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

@ApplicationScoped
public class EventStoreImpl implements EventStore {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreImpl.class);
    private final LocalFileSystem extern;
    private final Serializer serializer;

    @Inject
    public EventStoreImpl(Serializer serializer) {
        var configuration = new Configuration().scope("eventstore");
        this.extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        this.serializer = serializer;
    }

    @Override
    public <T extends Event> void save(List<T> events) {
        var timestamp = now(ZoneId.of("Asia/Tokyo")).format(ofPattern("yyyyMMddHHmmssSSS"));
        for (var i = 0; i < events.size(); i++) {
            var event = events.get(i);
            var sequence = String.format("%s_%03d", timestamp, i);
            var filename = sequence + "_" + this.snakeCase(event.id()) + "_" + event.getClass().getSimpleName() + ".json";
            var uri = this.extern.resolve(filename);
            var json = this.serializer.serialize(event);
            this.extern.put(new JsonResource(uri, json));
        }
    }

    @Override
    public List<Event> eventsForAggregate(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> allEvents() {
        throw new UnsupportedOperationException();
    }

    private String snakeCase(String id) {
        return id.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
    }
}
