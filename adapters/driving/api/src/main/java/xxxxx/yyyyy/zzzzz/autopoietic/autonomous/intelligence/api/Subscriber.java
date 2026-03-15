package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import java.util.concurrent.TimeUnit;

public interface Subscriber extends AutoCloseable {
    String poll(int timeout, TimeUnit unit);

    @Override
    void close();
}
