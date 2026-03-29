package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface EventPublisher {
    <T extends Event> void publish(T event);
}
