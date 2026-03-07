package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Utility {
    public static Object invoke(Method method, Object target) {
        try {
            return method.invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Method> accessors(Class<?> type) {
        return Stream.of(type.getMethods())
            .filter(x -> x.getParameterCount() == 0
                && !x.getDeclaringClass().equals(Object.class));
    }

    public static String loadResource(String name) {
        try (var stream = Utility.class.getClassLoader().getResourceAsStream(name)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /// Fully Qualified Class Name
    public static String toFqcn(String package_, String id) {
        return package_ + "." + id;
    }

    public static String toSnakeCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        var result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else if (!Character.isLetterOrDigit(c)) {
                result.append('_');
            } else {
                result.append(c);
            }
        }
        return result.toString()
            .replaceAll("_+", "_").replaceAll("^_+|_+$", "");
    }
}
