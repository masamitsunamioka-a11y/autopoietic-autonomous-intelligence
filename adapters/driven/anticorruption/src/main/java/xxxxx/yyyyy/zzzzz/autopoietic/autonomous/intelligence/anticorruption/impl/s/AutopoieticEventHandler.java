package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.NewArea;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.NewEffector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.NewNeuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

@ApplicationScoped
public class AutopoieticEventHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutopoieticEventHandler.class);
    private final Adapter<Area> areaAdapter;
    private final Adapter<Neuron> neuronAdapter;
    private final Adapter<Effector> effectorAdapter;

    @Inject
    public AutopoieticEventHandler(Adapter<Area> areaAdapter,
                                   Adapter<Neuron> neuronAdapter,
                                   Adapter<Effector> effectorAdapter) {
        this.areaAdapter = areaAdapter;
        this.neuronAdapter = neuronAdapter;
        this.effectorAdapter = effectorAdapter;
    }

    public void handle(@Observes Event event) {
        switch (event) {
            case AreaCreated x -> this.areaAdapter.publish(new NewArea(x.id(), x.tuning(), x.neurons()));
            case AreaRemoved x -> this.areaAdapter.revoke(x.id());
            case NeuronCreated x -> this.neuronAdapter.publish(new NewNeuron(x.id(), x.tuning(), x.effectors()));
            case NeuronRemoved x -> this.neuronAdapter.revoke(x.id());
            case EffectorCreated x -> this.effectorAdapter.publish(new NewEffector(x.id(), x.program()));
            default -> {
            }
        }
    }
}
