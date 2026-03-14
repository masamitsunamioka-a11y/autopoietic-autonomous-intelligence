package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class Events extends LinkedBlockingQueue<String> {
    private static final Logger logger = LoggerFactory.getLogger(Events.class);
    private static final String POISON = "__CLOSE__";
    private final Gson gson = new Gson();

    public void queue(Event event) {
        this.offer(this.gson.toJson(event));
    }

    public void close() {
        this.offer(POISON);
    }

    public boolean isPoison(String value) {
        return POISON.equals(value);
    }
}
