package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.*;

public class JavaTranslator<I extends Entity, E extends Resource> implements Translator<I, E> {
    private static final Logger logger = LoggerFactory.getLogger(JavaTranslator.class);
    private final ProxyProvider<I> proxyProvider;
    private final Class<I> type;
    private final String package_;
    private final String template;

    public JavaTranslator(ProxyProvider<I> proxyProvider, Class<I> type) {
        this.proxyProvider = proxyProvider;
        this.type = type;
        var configuration = new Configuration().neural(type);
        this.package_ = configuration.get("package");
        this.template = loadResource("effector.java.template");
    }

    @Override
    public I internalize(E resource) {
        return this.proxyProvider.provide(resource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E externalize(I object) {
        var data = accessors(this.type).reduce(
            this.template.replace("{{package}}", this.package_),
            (x, y) -> x.replace(
                "{{" + y.getName() + "}}",
                String.valueOf(invoke(y, object))),
            (x, y) -> x);
        return (E) new FileResource(data);
    }
}
