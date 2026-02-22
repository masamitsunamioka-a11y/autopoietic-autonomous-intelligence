package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

/// Agentforce: Topic
public interface Receptor {
    String name();

    String label();

    String description();

    String instructions();

    List<Effector> effectors();
}
