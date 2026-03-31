package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.CommandHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventStore;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.AggregateNotFoundException;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.StrengthDecayed;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceEncoded;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceRemoved;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.DecayEpisode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.EncodeEpisode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class EpisodicCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(EpisodicCommandHandler.class);
    private final LocalFileSystem extern;
    private final Serializer serializer;
    private final double decayFactor;
    private final EventStore eventStore;

    @Inject
    public EpisodicCommandHandler(Serializer serializer, EventStore eventStore) {
        var configuration = new Configuration().scope("hippocampal").scope("episode");
        this.extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        this.serializer = serializer;
        this.decayFactor = Double.parseDouble(configuration.get("decay"));
        this.eventStore = eventStore;
    }

    public void handle(@Observes EncodeEpisode command) {
        this.onEncode(command.payload());
    }

    public void handle(@Observes DecayEpisode command) {
        this.onDecay();
    }

    private void onEncode(Trace trace) {
        var aggregateId = "hippocampal/episode/" + trace.id();
        var lastVersion = 0;
        try {
            var existing = this.eventStore.eventsForAggregate(aggregateId);
            lastVersion = existing.getLast().version();
        } catch (AggregateNotFoundException e) {
            lastVersion = 0;
        }
        var event = new TraceEncoded(trace.id(), System.currentTimeMillis(), lastVersion + 1, trace.content());
        this.eventStore.save(aggregateId, List.of(event), lastVersion);
    }

    private void onDecay() {
        var now = System.currentTimeMillis();
        this.extern.set()
            .forEach(x -> {
                var resource = (JsonResource) this.extern.get(x);
                var engram = this.serializer.<EngramImpl>deserialize(resource.content(), EngramImpl.class);
                var weakened = engram.strength() * this.decayFactor;
                var id = engram.trace().id();
                var event = weakened < 0.01
                    ? (Event) new TraceRemoved(id, now, 0)
                    : new StrengthDecayed(id, now, 0, weakened);
                this.eventStore.save("hippocampal/episode/" + id, List.of(event), -1);
            });
    }

    private record EngramImpl(double strength, TraceImpl trace) {
    }
}
