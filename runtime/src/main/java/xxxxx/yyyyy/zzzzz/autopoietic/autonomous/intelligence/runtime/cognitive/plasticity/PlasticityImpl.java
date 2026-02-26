package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.plasticity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class PlasticityImpl implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PlasticityImpl.class);
    private final Nucleus nucleus;
    private final Transducer transducer;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Module> moduleRepository;
    private final Repository<Effector> effectorRepository;

    @Inject
    public PlasticityImpl(Nucleus nucleus,
                          Transducer transducer,
                          Repository<Neuron> neuronRepository,
                          Repository<Module> moduleRepository,
                          Repository<Effector> effectorRepository) {
        this.nucleus = nucleus;
        this.transducer = transducer;
        this.neuronRepository = neuronRepository;
        this.moduleRepository = moduleRepository;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public void potentiate(Stimulus stimulus) {
        var signal = this.transducer.potentiation(stimulus);
        var output = this.nucleus.compute(signal, Potentiation.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], " +
                "NewDisposition: {} chars, " +
                "NewNeurons: {}, NewModules: {}, NewEffectors: {}",
            output.confidence(),
            output.reasoning(),
            output.newDisposition().length(),
            output.newNeurons().size(),
            output.newModules().size(),
            output.newEffectors().size()
        );
        this.reinforce(output, stimulus.neuron());
        this.sprout(output);
    }

    @Override
    public void prune() {
        var signal = this.transducer.pruning();
        var output = this.nucleus.compute(signal, Pruning.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], " +
                "MergedNeurons: {}, MergedModules: {}",
            output.confidence(),
            output.reasoning(),
            output.mergedNeurons().size(),
            output.mergedModules().size()
        );
        this.eliminate(output);
        this.consolidate(output);
    }

    private void reinforce(Potentiation potentiation, Neuron neuron) {
        if (!potentiation.newDisposition().isEmpty()) {
            this.neuronRepository.store(new Potentiation.Neuron(
                neuron.name(),
                neuron.function(),
                potentiation.newDisposition(),
                neuron.modules().stream()
                    .map(Module::name)
                    .toList()));
        }
    }

    private void sprout(Potentiation potentiation) {
        potentiation.newEffectors()
            .forEach(this.effectorRepository::store);
        potentiation.newModules().stream()
            .filter(this::allEffectorsExist)
            .forEach(this.moduleRepository::store);
        potentiation.newNeurons().stream()
            .filter(this::allModulesExist)
            .forEach(this.neuronRepository::store);
    }

    private boolean allEffectorsExist(Potentiation.Module module) {
        var known = this.effectorRepository.findAll().stream().map(Effector::name).collect(toSet());
        var missing = module.effectors().stream().filter(n -> !known.contains(n)).toList();
        if (!missing.isEmpty())
            logger.warn("[PLASTICITY] Module '{}' skipped: missing effectors {}", module.name(), missing);
        return missing.isEmpty();
    }

    private boolean allModulesExist(Potentiation.Neuron neuron) {
        var known = this.moduleRepository.findAll().stream().map(Module::name).collect(toSet());
        var missing = neuron.modules().stream().filter(n -> !known.contains(n)).toList();
        if (!missing.isEmpty())
            logger.warn("[PLASTICITY] Neuron '{}' skipped: missing modules {}", neuron.name(), missing);
        return missing.isEmpty();
    }

    private void eliminate(Pruning pruning) {
        pruning.mergedNeurons().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.neuronRepository::remove);
        pruning.mergedModules().stream()
            .flatMap(x -> x.sources().stream())
            .forEach(this.moduleRepository::remove);
    }

    private void consolidate(Pruning pruning) {
        pruning.mergedModules().stream()
            .map(Pruning.MergedModule::result)
            .forEach(this.moduleRepository::store);
        pruning.mergedNeurons().stream()
            .map(Pruning.MergedNeuron::result)
            .forEach(this.neuronRepository::store);
    }
}
