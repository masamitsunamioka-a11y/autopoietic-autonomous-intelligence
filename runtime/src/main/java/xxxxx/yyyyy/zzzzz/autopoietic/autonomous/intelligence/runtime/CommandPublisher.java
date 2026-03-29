package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

public interface CommandPublisher {
    <T extends Command> void publish(T command);
}
