package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Thalamus;

@ApplicationScoped
public class PureJavaThalamus implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaThalamus.class);
    private static final String START_NEURON = "StartNeuron";
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Repository<Neuron> neuronRepository;

    @Inject
    public PureJavaThalamus(Intelligence intelligence,
                            PromptAssembler promptAssembler,
                            Repository<Neuron> neuronRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.neuronRepository = neuronRepository;
    }

    @Override
    public Neuron relay(Context context) {
        var prompt = this.promptAssembler.relay(context);
        var direction = this.intelligence.reason(prompt, Direction.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], SelectedNeuron: {}",
            direction.confidence(),
            direction.reasoning(),
            direction.neuron()
        );
        var neuron = this.neuronRepository.find(direction.neuron());
        if (neuron == null) {
            throw new IllegalStateException();
        }
        return neuron;
    }
}
