package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class SpinLock {
    private static final Logger logger = LoggerFactory.getLogger(SpinLock.class);

    public void await(Callable<Boolean> condition, int retries, long interval) {
        for (int i = 0; i < retries; i++) {
            try {
                if (Boolean.TRUE.equals(condition.call())) {
                    return;
                }
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
