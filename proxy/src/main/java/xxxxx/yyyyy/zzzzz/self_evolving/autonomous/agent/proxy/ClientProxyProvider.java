package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy;

public interface ClientProxyProvider {
    <T> T provide(Contextual<T> contextual);
}
