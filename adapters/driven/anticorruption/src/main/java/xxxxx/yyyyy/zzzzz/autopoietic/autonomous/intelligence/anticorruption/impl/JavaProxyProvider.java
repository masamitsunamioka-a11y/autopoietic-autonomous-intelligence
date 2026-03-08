package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.*;

public class JavaProxyProvider<I extends Entity> implements ProxyProvider<I> {
    private static final Logger logger = LoggerFactory.getLogger(JavaProxyProvider.class);
    private static final Pattern CLASS_NAME = Pattern.compile("public class (\\w+)");
    private final Serializer serializer;
    private final Class<I> type;
    private final String package_;
    private final String classpath;

    public JavaProxyProvider(Serializer serializer, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
        var configuration = new Configuration().neural(type);
        this.package_ = configuration.get("package");
        this.classpath = configuration.get("compiler.classpath");
    }

    @Override
    @SuppressWarnings("unchecked")
    public I provide(Resource resource) {
        var name = this.extractName(resource.data());
        var clazz = this.compile(name, resource.data());
        var instance = this.instantiate(clazz);
        var map = accessors(this.type).collect(Collectors.toMap(
            Method::getName,
            x -> invoke(x, instance)));
        var string = this.serializer.serialize(map);
        return (I) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{this.type},
            (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "toString" -> string;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> {
                        yield args != null && args.length == 1 && proxy == args[0];
                    }
                    default -> method.invoke(instance, args);
                };
            }
        );
    }

    private String extractName(String source) {
        return CLASS_NAME.matcher(source).results()
            .findFirst()
            .orElseThrow()
            .group(1);
    }

    private Class<?> compile(String name, String source) {
        var fqcn = toFqcn(this.package_, name);
        var javaCompiler = ToolProvider.getSystemJavaCompiler();
        var javaFileManager = new InMemoryJavaFileManager(
            javaCompiler.getStandardFileManager(null, null, null));
        var javaFileObject = new InMemoryJavaFileObject(
            URI.create("string:///" + fqcn.replace(".", "/") + ".java"), source);
        var task = javaCompiler.getTask(
            null, javaFileManager, null,
            List.of("-classpath", this.fullClasspath(), "-Xlint:none"),
            null, List.of(javaFileObject));
        if (!task.call()) {
            throw new IllegalStateException("Compile failed: " + fqcn);
        }
        try {
            return javaFileManager.loadClass(fqcn);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String fullClasspath() {
        var runtime = System.getProperty("java.class.path");
        return this.classpath + ":" + runtime;
    }

    private I instantiate(Class<?> clazz) {
        try {
            return this.type.cast(clazz.getConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
