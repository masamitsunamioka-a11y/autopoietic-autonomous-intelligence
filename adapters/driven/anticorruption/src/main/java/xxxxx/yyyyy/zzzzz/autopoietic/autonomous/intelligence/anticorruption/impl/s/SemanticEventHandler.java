package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceEncoded;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceRemoved;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

@ApplicationScoped
public class SemanticEventHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(SemanticEventHandler.class);
    private final Adapter<Trace> adapter;

    @Inject
    public SemanticEventHandler(@Semantic Adapter<Trace> adapter) {
        this.adapter = adapter;
    }

    public void handle(@Observes Event event) {
        switch (event) {
            case TraceEncoded x -> this.adapter.publish(new TraceImpl(x.id(), x.content()));
            case TraceRemoved x -> this.adapter.revoke(x.id());
            default -> {
            }
        }
    }
}
