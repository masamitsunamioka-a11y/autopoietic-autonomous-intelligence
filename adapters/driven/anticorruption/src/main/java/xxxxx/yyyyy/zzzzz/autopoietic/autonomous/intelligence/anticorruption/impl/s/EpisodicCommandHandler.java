package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
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
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EpisodicCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(EpisodicCommandHandler.class);
    private final LocalFileSystem extern;
    private final Serializer serializer;
    private final double decayFactor;
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    @Inject
    public EpisodicCommandHandler(Serializer serializer, EventStore eventStore,
                                  EventPublisher eventPublisher) {
        var configuration = new Configuration().scope("hippocampal").scope("episode");
        this.extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        this.serializer = serializer;
        this.decayFactor = Double.parseDouble(configuration.get("decay"));
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void handle(@Observes EncodeEpisode command) {
        this.onEncode(command.payload());
    }

    public void handle(@Observes DecayEpisode command) {
        this.onDecay();
    }

    private void onEncode(Trace trace) {
        var events = List.of(new TraceEncoded(trace.id(), System.currentTimeMillis(), trace.content()));
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }

    private void onDecay() {
        var events = new ArrayList<Event>();
        var now = System.currentTimeMillis();
        this.extern.set().forEach(x -> {
            var resource = (JsonResource) this.extern.get(x);
            var engram = this.serializer.<EngramImpl>deserialize(resource.content(), EngramImpl.class);
            var weakened = engram.strength() * this.decayFactor;
            if (weakened < 0.01) {
                events.add(new TraceRemoved(engram.trace().id(), now));
            } else {
                events.add(new StrengthDecayed(engram.trace().id(), now, weakened));
            }
        });
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }

    private record EngramImpl(double strength, TraceImpl trace) {
    }
}
