package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

public interface Schema {
    String name();

    String description();

    String protocol();

    List<Effector> effectors();
}
