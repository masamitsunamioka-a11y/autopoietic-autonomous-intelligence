package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

import java.util.List;

public interface Topic {
    String name();

    String label();

    String description();

    @Evolvable
    String instructions();

    @Evolvable
    void instructions(String instructions);

    List<String> actions();
}
