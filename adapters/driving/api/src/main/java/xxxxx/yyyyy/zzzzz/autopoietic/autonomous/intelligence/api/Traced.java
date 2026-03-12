package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Traced {
    final class Literal
        extends AnnotationLiteral<Traced>
        implements Traced {
        public static final Literal INSTANCE = new Literal();
    }
}
