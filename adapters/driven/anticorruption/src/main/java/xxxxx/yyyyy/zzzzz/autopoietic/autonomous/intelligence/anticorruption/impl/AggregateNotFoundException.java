package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException(String aggregateId) {
        super("Aggregate not found: " + aggregateId);
    }
}
