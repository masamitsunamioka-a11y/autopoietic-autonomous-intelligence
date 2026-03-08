package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;
import java.util.Map;

/// Neural tree snapshot for JSON serialization.
public record NeuralTree(List<Node> areas,
                         List<Leaf> neurons,
                         List<Leaf> effectors) {
    public record Leaf(String id, long timestamp) {
    }

    public record Node(String id, List<Leaf> neurons,
                       List<Leaf> effectors,
                       long timestamp) {
    }

    public NeuralTree(Repository<Area> areaRepository,
                      Repository<Neuron> neuronRepository,
                      Repository<Effector> effectorRepository,
                      Map<String, Long> timestamps) {
        this(
            areaRepository.findAll().stream()
                .map(a -> new Node(
                    a.id(),
                    a.neurons().stream()
                        .map(n -> new Leaf(
                            n, timestamps.getOrDefault(
                            n, 0L)))
                        .toList(),
                    a.effectors().stream()
                        .map(e -> new Leaf(
                            e, timestamps.getOrDefault(
                            e, 0L)))
                        .toList(),
                    timestamps.getOrDefault(a.id(), 0L)))
                .toList(),
            neuronRepository.findAll().stream()
                .map(n -> new Leaf(
                    n.id(), timestamps.getOrDefault(
                    n.id(), 0L)))
                .toList(),
            effectorRepository.findAll().stream()
                .map(e -> new Leaf(
                    e.id(), timestamps.getOrDefault(
                    e.id(), 0L)))
                .toList());
    }
}
