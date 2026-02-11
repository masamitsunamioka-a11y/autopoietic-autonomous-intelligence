package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import java.util.List;

public interface Repository<I> {
    List<I> findAll();

    I findByName(String name);

    void store(I internal);

    void store(String identifier, I internal);

    void store(String identifier, String internal);
}
