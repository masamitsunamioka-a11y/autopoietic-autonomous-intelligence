package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

public interface Publisher {
    void publish(Event event);

    Subscriber subscribe();

    boolean isPoison(String value);

    void close();
}
