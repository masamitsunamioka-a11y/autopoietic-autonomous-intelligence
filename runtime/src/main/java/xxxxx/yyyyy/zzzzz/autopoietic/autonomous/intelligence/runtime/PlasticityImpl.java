package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

@ApplicationScoped
public class PlasticityImpl implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PlasticityImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Schema> schemaRepository;
    private final Repository<Effector> effectorRepository;

    @Inject
    public PlasticityImpl(Nucleus nucleus,
                          Encoder encoder,
                          Repository<Neuron> neuronRepository,
                          Repository<Schema> schemaRepository,
                          Repository<Effector> effectorRepository) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.neuronRepository = neuronRepository;
        this.schemaRepository = schemaRepository;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public void potentiate(Impulse impulse) {
        var encoding = this.encoder.potentiation(impulse);
        var output = this.nucleus.compute(encoding, Potentiation.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], " +
                "NewInstructions: {} chars, " +
                "NewNeurons: {}, NewSchemas: {}, NewEffectors: {}",
            output.confidence(),
            output.reasoning(),
            output.newProtocol().length(),
            output.newNeurons().size(),
            output.newSchemas().size(),
            output.newEffectors().size()
        );
        this.reinforce(output, impulse.neuron());
        this.sprout(output);
    }

    @Override
    public void prune() {
        var encoding = this.encoder.pruning();
        var output = this.nucleus.compute(encoding, Pruning.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], " +
                "MergedNeurons: {}, MergedSchemas: {}",
            output.confidence(),
            output.reasoning(),
            output.mergedNeurons().size(),
            output.mergedSchemas().size()
        );
        this.eliminate(output);
        this.consolidate(output);
    }

    private void reinforce(Potentiation potentiation, Neuron neuron) {
        if (!potentiation.newProtocol().isEmpty()) {
            this.neuronRepository.store(new Potentiation.Neuron(
                neuron.name(),
                neuron.description(),
                potentiation.newProtocol(),
                neuron.schemas().stream()
                    .map(Schema::name)
                    .toList()));
        }
    }

    private void sprout(Potentiation potentiation) {
        potentiation.newEffectors()
            .forEach(this.effectorRepository::store);
        potentiation.newSchemas()
            .forEach(this.schemaRepository::store);
        potentiation.newNeurons()
            .forEach(this.neuronRepository::store);
    }

    private void eliminate(Pruning pruning) {
        pruning.mergedNeurons().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.neuronRepository::remove);
        pruning.mergedSchemas().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.schemaRepository::remove);
    }

    private void consolidate(Pruning pruning) {
        pruning.mergedSchemas().stream()
            .map(Pruning.MergedSchema::result)
            .forEach(this.schemaRepository::store);
        pruning.mergedNeurons().stream()
            .map(Pruning.MergedNeuron::result)
            .forEach(this.neuronRepository::store);
    }
}
