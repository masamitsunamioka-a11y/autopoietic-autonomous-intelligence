package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Repository;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Action;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public class DynamicProxyFactory {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private final Repository<Action<?>> actionRepository;

    @Inject
    public DynamicProxyFactory(Repository<Action<?>> actionRepository) {
        this.actionRepository = actionRepository;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz, Map<String, Object> data) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = new Class<?>[]{clazz};
        return (T) Proxy.newProxyInstance(loader, interfaces, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "toString" -> GSON.toJson(data);
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> arguments != null && arguments.length == 1 && proxy == arguments[0];
                default -> {
                    String name = method.getName();
                    String key = (name.startsWith("get"))
                            ? Character.toLowerCase(name.charAt(3)) + name.substring(4)
                            : name;
                    Object value = data.get(key);
                    if (value == null && Action.class.isAssignableFrom(method.getReturnType())) {
                        value = this.actionRepository.findByName(key);
                        if (value == null) {
                            value = this.actionRepository.findByName(method.getReturnType().getSimpleName());
                        }
                    }
                    if (value instanceof Map map) {
                        yield this.create(method.getReturnType(), (Map<String, Object>) map);
                    }
                    if (value instanceof List list) {
                        yield list.stream()
                                .map(item -> (item instanceof Map m)
                                        ? this.create(this.getGenericType(method), (Map<String, Object>) m)
                                        : item)
                                .toList();
                    }
                    yield value;
                }
            };
        });
    }

    private Class<?> getGenericType(Method method) {
        Type type = method.getGenericReturnType();
        return (type instanceof ParameterizedType pt)
                ? this.resolve(pt.getActualTypeArguments()[0])
                : Object.class;
    }

    private Class<?> resolve(Type type) {
        return switch (type) {
            case Class<?> clazz -> clazz;
            case ParameterizedType parameterizedType -> this.resolve(parameterizedType.getRawType());
            case WildcardType wildcardType -> this.resolve(wildcardType.getUpperBounds()[0]);
            default -> Object.class;
        };
    }
}
