package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class InMemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, byte[]> classes;

    public InMemoryJavaFileManager(StandardJavaFileManager delegate) {
        super(delegate);
        this.classes = new HashMap<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String name,
                                               JavaFileObject.Kind kind,
                                               FileObject sibling) {
        return new SimpleJavaFileObject(
            URI.create("bytes:///" + name.replace(".", "/") + ".class"), kind) {
            @Override
            public OutputStream openOutputStream() {
                return new ByteArrayOutputStream() {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        InMemoryJavaFileManager.this.classes.put(
                            name, this.toByteArray());
                    }
                };
            }
        };
    }

    public Class<?> loadClass(String fqcn) throws ClassNotFoundException {
        var classLoader = Thread.currentThread().getContextClassLoader();
        return new ClassLoader(classLoader) {
            @Override
            protected Class<?> findClass(String name) {
                var bytes = InMemoryJavaFileManager.this.classes.get(name);
                return defineClass(name, bytes, 0, bytes.length);
            }
        }.loadClass(fqcn);
    }
}
