package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/conversation
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/context/Conversation.java
public interface Conversation {
    void begin();

    void begin(String id);

    void end();

    boolean isTransient();
}
