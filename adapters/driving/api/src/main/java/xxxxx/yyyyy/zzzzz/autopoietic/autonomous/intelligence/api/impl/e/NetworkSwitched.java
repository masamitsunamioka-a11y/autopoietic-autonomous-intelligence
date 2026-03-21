package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record NetworkSwitched(
    String type,
    String status
) implements Event {
    public NetworkSwitched(String status) {
        this("network-switched", status);
    }
}
