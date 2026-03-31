package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Message;

public interface Event extends Message {
    String id();

    long occurredOn();

    int version();
}
