package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/util/typeliteral
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/util/TypeLiteral.java
public abstract class TypeLiteral<T> {
    private final Type type;

    protected TypeLiteral() {
        Type superclass = this.getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType parameterizedType) {
            this.type = parameterizedType.getActualTypeArguments()[0];
        } else {
            throw new RuntimeException(
                    "Invalid TypeLiteral usage. Must be anonymous.");
        }
    }

    public Type type() {
        return this.type;
    }
}
