package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record MnemonicEvent(
    String type,
    Object content) implements Event {
    public MnemonicEvent(Object content) {
        this("mnemonic", content);
    }
}
