package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.stream.Collectors;

@ApplicationScoped
public class PureJavaPlasticity implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaPlasticity.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Receptor> receptorRepository;
    private final Repository<Effector> effectorRepository;

    @Inject
    public PureJavaPlasticity(Intelligence intelligence,
                              PromptAssembler promptAssembler,
                              Repository<Neuron> neuronRepository,
                              Repository<Receptor> receptorRepository,
                              Repository<Effector> effectorRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.neuronRepository = neuronRepository;
        this.receptorRepository = receptorRepository;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public void potentiate(Context context, Neuron neuron) {
        var prompt = this.promptAssembler.potentiation(context, neuron);
        var potentiation = this.intelligence.reason(prompt, Potentiation.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                "NewInstructions: {} chars, NewNeurons: {}, NewReceptors: {}, NewEffectors: {}",
            potentiation.confidence(),
            potentiation.reasoning(),
            potentiation.newInstructions().length(),
            potentiation.newNeurons().size(),
            potentiation.newReceptors().size(),
            potentiation.newEffectors().size()
        );
        this.storeEffectors(potentiation);
        this.storeReceptors(potentiation);
        this.storeNeurons(potentiation);
        this.storeInstructions(potentiation, neuron);
    }

    @Override
    public void prune() {
        var prompt = this.promptAssembler.pruning();
        var pruning = this.intelligence.reason(prompt, Pruning.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                "MergedNeurons: {}, MergedReceptors: {}",
            pruning.confidence(),
            pruning.reasoning(),
            pruning.mergedNeurons().size(),
            pruning.mergedReceptors().size()
        );
        this.removeSources(pruning);
        this.storeMerged(pruning);
    }

    private void storeEffectors(Potentiation potentiation) {
        potentiation.newEffectors().forEach(x -> {
            this.effectorRepository.store(x.name(), x);
        });
    }

    private void storeReceptors(Potentiation potentiation) {
        potentiation.newReceptors().forEach(x -> {
            this.receptorRepository.store(x.name(), x);
        });
    }

    private void storeNeurons(Potentiation potentiation) {
        potentiation.newNeurons().forEach(x -> {
            this.neuronRepository.store(x.name(), x);
        });
    }

    private void storeInstructions(Potentiation potentiation, Neuron neuron) {
        if (!potentiation.newInstructions().isEmpty()) {
            this.neuronRepository.store(neuron.name(), () -> """
                {
                  "name": "%s",
                  "label": "%s",
                  "description": "%s",
                  "instructions": "%s",
                  "receptors": %s
                }
                """.formatted(neuron.name(), neuron.label(), neuron.description(),
                potentiation.newInstructions()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n"),
                neuron.receptors().stream()
                    .map(x -> "\"" + x.name() + "\"")
                    .collect(Collectors.joining(", ", "[", "]"))
            ));
        }
    }

    private void removeSources(Pruning pruning) {
        pruning.mergedNeurons().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.neuronRepository::remove);
        pruning.mergedReceptors().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.receptorRepository::remove);
    }

    private void storeMerged(Pruning pruning) {
        pruning.mergedReceptors().stream()
            .map(Pruning.MergedReceptor::result)
            .forEach(x -> {
                this.receptorRepository.store(x.name(), x);
            });
        pruning.mergedNeurons().stream()
            .map(Pruning.MergedNeuron::result)
            .forEach(x -> {
                this.neuronRepository.store(x.name(), x);
            });
    }
}
