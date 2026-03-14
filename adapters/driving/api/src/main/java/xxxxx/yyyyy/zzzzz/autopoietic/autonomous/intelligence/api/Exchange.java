package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

public interface Exchange {
    String method();

    Exchange header(String key, String value);

    void send(int status);

    void send(int status, Object body);

    <T> T body(Class<T> type);

    void flush(String text);
}
