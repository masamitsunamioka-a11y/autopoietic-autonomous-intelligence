package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

/// Agentforce: Agent
public interface Neuron {
    String name();

    String label();

    String description();

    String instructions();

    List<Receptor> receptors();
}
