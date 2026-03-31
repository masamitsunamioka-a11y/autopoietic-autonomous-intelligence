package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

public class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String aggregateId, int expected, int actual) {
        super("Concurrency conflict for " + aggregateId + ": expected version " + expected + " but was " + actual);
    }
}
