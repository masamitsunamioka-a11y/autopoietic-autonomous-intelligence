package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.CommandHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventStore;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.AggregateNotFoundException;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceEncoded;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceRemoved;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.DecayKnowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.EncodeKnowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class SemanticCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(SemanticCommandHandler.class);
    private final Adapter<Trace> adapter;
    private final EventStore eventStore;

    @Inject
    public SemanticCommandHandler(@Semantic Adapter<Trace> adapter,
                                  EventStore eventStore) {
        this.adapter = adapter;
        this.eventStore = eventStore;
    }

    public void handle(@Observes EncodeKnowledge command) {
        this.onEncode(command.payload());
    }

    public void handle(@Observes DecayKnowledge command) {
        this.onDecay();
    }

    private void onEncode(Trace trace) {
        var aggregateId = "neocortical/knowledge/" + trace.id();
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
        var all = this.adapter.fetchAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var reversed = all.reversed();
        reversed.stream()
            .filter(x -> !seen.add(x.id()))
            .map(Trace::id)
            .forEach(x -> {
                var event = new TraceRemoved(x, System.currentTimeMillis(), 0);
                this.eventStore.save("neocortical/knowledge/" + x, List.of(event), -1);
            });
    }
}
