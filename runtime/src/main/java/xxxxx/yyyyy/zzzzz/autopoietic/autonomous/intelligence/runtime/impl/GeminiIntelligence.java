package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Intelligence;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class GeminiIntelligence implements Intelligence {
    private static final Logger logger = LoggerFactory.getLogger(GeminiIntelligence.class);
    private final Gson gson;
    private final GeminiClient geminiClient;
    private final Validator validator;

    public GeminiIntelligence() {
        this.gson = new GsonBuilder().serializeNulls().create();
        this.geminiClient = new GeminiClient();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T reason(String prompt, Class<T> type) {
        String schemaInstruction = this.buildSchemaInstruction(type);
        String augmentedPrompt = prompt + "\n\n" + schemaInstruction;
        String payload = this.geminiClient.post(augmentedPrompt);
        String cleanedJson = Util.cleanJson(payload);
        T result = this.gson.fromJson(cleanedJson, type);
        Set<ConstraintViolation<T>> violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            String reasons = violations.stream()
                    .map(x -> String.format("[%s] %s", x.getPropertyPath(), x.getMessage()))
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("""
                    [Integrity Violation] Intelligence produced a malformed conclusion for %s.
                    Violations: %s
                    """.formatted(type.getSimpleName(), reasons));
        }
        return result;
    }

    private String buildSchemaInstruction(Class<?> type) {
        String jsonSkeleton = this.gson.toJson(this.createEmptyStub(type));
        return """
                ## [MANDATORY OUTPUT FORMAT: %s]
                You MUST return a valid JSON object strictly following this structure:
                ```json
                %s
                ```
                CRITICAL: Return ONLY the raw JSON object. No conversational filler.
                """.formatted(type.getSimpleName(), jsonSkeleton);
    }

    private Object createEmptyStub(Class<?> type) {
        if (!type.isRecord()) return null;
        RecordComponent[] components = type.getRecordComponents();
        Object[] args = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent rc = components[i];
            Class<?> t = rc.getType();
            String constraints = Arrays.stream(rc.getAnnotations())
                    .map(this::getConstraintHint)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            String hint = constraints.isEmpty() ? "" : "[" + constraints + "] ";
            args[i] = switch (t) {
                case Class<?> c when c.equals(String.class) -> hint + "(String)";
                case Class<?> c when List.class.isAssignableFrom(c) -> {
                    ParameterizedType pt = (ParameterizedType) rc.getGenericType();
                    Class<?> actualType = (Class<?>) pt.getActualTypeArguments()[0];
                    List<Object> list = new ArrayList<>();
                    list.add(actualType.isRecord() ? createEmptyStub(actualType) : hint + "(Element)");
                    yield list;
                }
                case Class<?> c when c.isEnum() -> hint + "(" + Arrays.stream(c.getEnumConstants())
                        .map(Object::toString).collect(Collectors.joining("|")) + ")";
                case Class<?> c when c.isRecord() -> createEmptyStub(c);
                case Class<?> c when c == double.class || c == float.class -> 0.0;
                case Class<?> c when c == int.class || c == long.class -> 0;
                default -> null;
            };
        }
        try {
            Class<?>[] argTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new);
            return type.getDeclaredConstructor(argTypes).newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("[Integrity Violation] Stub creation failed: " + type.getName(), e);
        }
    }

    private String getConstraintHint(Annotation anno) {
        return switch (anno) {
            case NotBlank _ -> "NOT_BLANK";
            case NotEmpty _ -> "NOT_EMPTY";
            case DecimalMin m -> "MIN:" + m.value();
            case DecimalMax m -> "MAX:" + m.value();
            case NotNull _ -> "NOT_NULL";
            default -> null;
        };
    }
}
