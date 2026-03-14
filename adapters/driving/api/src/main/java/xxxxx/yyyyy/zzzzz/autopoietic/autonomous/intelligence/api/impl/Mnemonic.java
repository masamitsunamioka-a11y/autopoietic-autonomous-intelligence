package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Mnemonic {
    final class Literal
        extends AnnotationLiteral<Mnemonic>
        implements Mnemonic {
        public static final Literal INSTANCE = new Literal();
    }
}
