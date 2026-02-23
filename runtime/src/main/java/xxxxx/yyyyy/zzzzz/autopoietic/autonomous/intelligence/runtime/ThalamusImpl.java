package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Thalamus;

@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Repository<Neuron> neuronRepository;

    @Inject
    public ThalamusImpl(Nucleus nucleus,
                        Encoder encoder,
                        Repository<Neuron> neuronRepository) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.neuronRepository = neuronRepository;
    }

    @Override
    public Impulse relay(Impulse impulse) {
        var encoding = this.encoder.relay(impulse);
        var output = this.nucleus.compute(encoding, Projection.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], SelectedNeuron: {}",
            output.confidence(),
            output.reasoning(),
            output.neuron()
        );
        var neuron = this.neuronRepository.find(output.neuron());
        if (neuron == null) {
            throw new IllegalStateException();
        }
        return new ImpulseImpl(impulse.input(), neuron);
    }
}
