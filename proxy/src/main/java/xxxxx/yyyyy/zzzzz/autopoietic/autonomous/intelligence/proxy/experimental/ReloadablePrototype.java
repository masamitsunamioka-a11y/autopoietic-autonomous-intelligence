package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.experimental;

import java.time.LocalTime;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReloadablePrototype {
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public void reload() {
        lock.writeLock().lock();
        try {
            this.log("reload start");
            Thread.sleep(5000);
            this.log("reload end");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public <T> T get(String id) {
        lock.readLock().lock();
        try {
            this.log("get");
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void log(String message) {
        System.out.printf("[%s] [%s] %s%n", LocalTime.now(), Thread.currentThread().getName(), message);
    }
}
