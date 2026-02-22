package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

public interface Agent {
    String name();

    String label();

    String description();

    String instructions();

    void instructions(String instructions);

    List<Topic> topics();
}
