package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventPublisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventStore;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

@ApplicationScoped
public class EventStoreImpl implements EventStore {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreImpl.class);
    private final Path basePath;
    private final LocalFileSystem extern;
    private final Serializer serializer;
    private final EventPublisher eventPublisher;

    @Inject
    public EventStoreImpl(Serializer serializer, EventPublisher eventPublisher) {
        var configuration = new Configuration().scope("eventstore");
        this.basePath = Path.of(configuration.get("path"), "");
        this.extern = new LocalFileSystem(this.basePath);
        this.serializer = serializer;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void save(String aggregateId, List<? extends Event> events, int expectedVersion) {
        if (expectedVersion != -1) {
            var existing = this.descriptorsFor(aggregateId);
            var lastVersion = existing.isEmpty() ? 0 : existing.getLast().version();
            if (lastVersion != expectedVersion) {
                throw new ConcurrencyException(aggregateId, expectedVersion, lastVersion);
            }
        }
        var timestamp = now(ZoneId.of("Asia/Tokyo")).format(ofPattern("yyyyMMddHHmmssSSS"));
        for (var i = 0; i < events.size(); i++) {
            var event = events.get(i);
            var sequence = String.format("%s_%03d", timestamp, i);
            var filename = aggregateId + "/" + sequence + "_" + event.getClass().getSimpleName() + ".json";
            var uri = this.extern.resolve(filename);
            var json = this.serializer.serialize(event);
            this.extern.put(new JsonResource(uri, json));
            this.eventPublisher.publish(event);
        }
    }

    @Override
    public List<Event> eventsForAggregate(String aggregateId) {
        var directory = this.basePath.resolve(aggregateId);
        if (!Files.exists(directory)) {
            throw new AggregateNotFoundException(aggregateId);
        }
        return this.descriptorsFor(aggregateId).stream()
            .map(EventDescriptor::event)
            .toList();
    }

    private List<EventDescriptor> descriptorsFor(String aggregateId) {
        var directory = this.basePath.resolve(aggregateId);
        if (!Files.exists(directory)) {
            return List.of();
        }
        var scope = new LocalFileSystem(directory);
        return scope.set().stream()
            .sorted()
            .map(x -> {
                var resource = (JsonResource) scope.get(x);
                var type = this.eventType(Path.of(x).getFileName().toString());
                var event = (Event) this.serializer.deserialize(resource.content(), type);
                return new EventDescriptor(aggregateId, event, event.version());
            })
            .toList();
    }

    /// @formatter:off
    private record EventDescriptor(String aggregateId, Event event, int version) {
    }
    /// @formatter:on
    /// @formatter:off
    private Class<? extends Event> eventType(String filename) {
        var typeName = filename.replaceAll(".*_(\\w+)\\.json$", "$1");
        return switch (typeName) {
            case "AreaCreated"      -> AreaCreated.class;
            case "AreaUpdated"      -> AreaUpdated.class;
            case "AreaRemoved"      -> AreaRemoved.class;
            case "NeuronCreated"    -> NeuronCreated.class;
            case "NeuronRemoved"    -> NeuronRemoved.class;
            case "EffectorCreated"  -> EffectorCreated.class;
            case "TraceEncoded"     -> TraceEncoded.class;
            case "TraceRemoved"     -> TraceRemoved.class;
            case "StrengthDecayed"  -> StrengthDecayed.class;
            default -> throw new IllegalArgumentException(typeName);
        };
    }
    /// @formatter:on
}
