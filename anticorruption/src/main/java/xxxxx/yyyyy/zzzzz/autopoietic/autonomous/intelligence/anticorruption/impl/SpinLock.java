package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class SpinLock {
    private static final Logger logger = LoggerFactory.getLogger(SpinLock.class);

    public void await(Callable<Boolean> condition, int retries, long interval) {
        for (int i = 0; i < retries; i++) {
            try {
                if (Boolean.TRUE.equals(condition.call())) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[SPINLOCK] Condition met after {} retries ({}ms total).", i + 1, i * interval);
                    }
                    return;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("[SPINLOCK] Condition not yet satisfied ({}/{}). Retrying in {}ms...", i + 1, retries, interval);
                }
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("SpinLock interrupted during execution.", e);
            } catch (Exception e) {
                throw new RuntimeException("Critical failure within SpinLock condition check.", e);
            }
        }
        logger.warn("[SPINLOCK] Timeout reached. Maximum retries ({}) exhausted.", retries);
    }
}
