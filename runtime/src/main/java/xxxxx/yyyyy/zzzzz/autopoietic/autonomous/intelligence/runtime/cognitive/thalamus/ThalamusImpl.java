package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.thalamus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Nucleus nucleus;
    private final Transducer transducer;
    private final Repository<Neuron> neuronRepository;

    @Inject
    public ThalamusImpl(Nucleus nucleus,
                        Transducer transducer,
                        Repository<Neuron> neuronRepository) {
        this.nucleus = nucleus;
        this.transducer = transducer;
        this.neuronRepository = neuronRepository;
    }

    @Override
    public Stimulus relay(Stimulus stimulus) {
        var signal = this.transducer.relay(stimulus);
        var output = this.nucleus.compute(signal, Projection.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], SelectedNeuron: {}",
            output.confidence(),
            output.reasoning(),
            output.neuron()
        );
        var neuron = this.neuronRepository.find(output.neuron());
        if (neuron == null) {
            throw new IllegalStateException();
        }
        return new StimulusImpl(stimulus.input(), neuron);
    }
}
