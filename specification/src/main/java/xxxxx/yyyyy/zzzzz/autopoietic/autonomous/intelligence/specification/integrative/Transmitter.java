package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Transmitter {
    <T> T transmit(Impulse impulse, Class<T> response);
}
