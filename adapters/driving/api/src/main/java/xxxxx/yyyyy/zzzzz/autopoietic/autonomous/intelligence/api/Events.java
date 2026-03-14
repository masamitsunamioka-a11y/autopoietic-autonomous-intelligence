package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import java.util.concurrent.TimeUnit;

public interface Events {
    void queue(Event event);

    String poll(int timeout, TimeUnit unit);

    boolean isPoison(String value);

    void close();
}
