package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

public interface Topic {
    String name();

    String label();

    String description();

    String instructions();

    List<Action> actions();
}
