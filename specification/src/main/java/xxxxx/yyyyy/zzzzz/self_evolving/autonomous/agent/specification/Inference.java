package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

public interface Inference {
    String agentName();

    String phase();

    String thought();

    String content();

    String action();

    double confidence();
}
