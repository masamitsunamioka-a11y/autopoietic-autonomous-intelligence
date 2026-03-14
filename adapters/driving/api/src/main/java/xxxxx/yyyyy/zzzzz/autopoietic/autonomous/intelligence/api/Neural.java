package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Neural {
    final class Literal
        extends AnnotationLiteral<Neural>
        implements Neural {
        public static final Literal INSTANCE = new Literal();
    }
}
