package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual;

import jakarta.inject.Qualifier;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Process {
    Percept handle(Impulse impulse, Decision decision);

    /// @formatter:off
    @Qualifier @Retention(RUNTIME) @Target({TYPE, METHOD, FIELD, PARAMETER})
    @interface Vocalize {}
    @Qualifier @Retention(RUNTIME) @Target({TYPE, METHOD, FIELD, PARAMETER})
    @interface Fire {}
    @Qualifier @Retention(RUNTIME) @Target({TYPE, METHOD, FIELD, PARAMETER})
    @interface Potentiate {}
    @Qualifier @Retention(RUNTIME) @Target({TYPE, METHOD, FIELD, PARAMETER})
    @interface Project {}
    @Qualifier @Retention(RUNTIME) @Target({TYPE, METHOD, FIELD, PARAMETER})
    @interface Inhibit {}
    /// @formatter:on
}
