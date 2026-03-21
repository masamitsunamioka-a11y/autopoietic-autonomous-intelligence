package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Dispatcher {
    record Pathway(
        Class<?> caller, Class<?> response, String template) {
    }

    Pathway dispatch(Impulse impulse);
}
