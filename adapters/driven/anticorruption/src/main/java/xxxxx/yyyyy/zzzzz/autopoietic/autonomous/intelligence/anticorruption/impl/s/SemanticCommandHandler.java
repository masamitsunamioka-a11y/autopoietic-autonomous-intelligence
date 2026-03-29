package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceEncoded;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceRemoved;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.DecayKnowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.EncodeKnowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class SemanticCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(SemanticCommandHandler.class);
    private final Adapter<Trace> adapter;
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    @Inject
    public SemanticCommandHandler(@Semantic Adapter<Trace> adapter,
                                  EventStore eventStore,
                                  EventPublisher eventPublisher) {
        this.adapter = adapter;
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void handle(@Observes EncodeKnowledge command) {
        this.onEncode(command.payload());
    }

    public void handle(@Observes DecayKnowledge command) {
        this.onDecay();
    }

    private void onEncode(Trace trace) {
        var events = List.<Event>of(new TraceEncoded(trace.id(), System.currentTimeMillis(), trace.content()));
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }

    private void onDecay() {
        var all = this.adapter.fetchAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var reversed = all.reversed();
        var expired = reversed.stream()
            .filter(x -> !seen.add(x.id()))
            .map(Trace::id)
            .toList();
        var events = new ArrayList<Event>();
        expired.forEach(x -> events.add(new TraceRemoved(x, System.currentTimeMillis())));
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }
}
