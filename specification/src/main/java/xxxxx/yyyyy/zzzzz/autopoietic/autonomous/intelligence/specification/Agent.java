package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.List;

public interface Agent {
    String name();

    String label();

    String description();

    @Evolvable
    String instructions();

    @Evolvable
    void instructions(String instructions);

    List<Topic> topics();
}
