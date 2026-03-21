package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import java.util.concurrent.Flow;

public interface Publisher extends Flow.Publisher<Event> {
    int submit(Event event);

    void close();
}
