package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;
import java.util.Map;

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
                .map(x -> new Node(
                    x.id(),
                    x.neurons().stream()
                        .map(y -> new Leaf(
                            y, timestamps.getOrDefault(
                            y, 0L)))
                        .toList(),
                    x.effectors().stream()
                        .map(z -> new Leaf(
                            z, timestamps.getOrDefault(
                            z, 0L)))
                        .toList(),
                    timestamps.getOrDefault(x.id(), 0L)))
                .toList(),
            neuronRepository.findAll().stream()
                .map(y -> new Leaf(
                    y.id(), timestamps.getOrDefault(
                    y.id(), 0L)))
                .toList(),
            effectorRepository.findAll().stream()
                .map(z -> new Leaf(
                    z.id(), timestamps.getOrDefault(
                    z.id(), 0L)))
                .toList());
    }
}
