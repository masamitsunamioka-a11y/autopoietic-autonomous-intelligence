package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

public interface ExternalService {
    <I, T> I call(String prompt, Class<T> type);
}
