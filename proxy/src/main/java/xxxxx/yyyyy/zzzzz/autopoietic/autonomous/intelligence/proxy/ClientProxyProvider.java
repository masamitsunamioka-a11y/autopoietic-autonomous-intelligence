package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

public interface ClientProxyProvider {
    <T> T provide(Contextual<T> contextual);
}
