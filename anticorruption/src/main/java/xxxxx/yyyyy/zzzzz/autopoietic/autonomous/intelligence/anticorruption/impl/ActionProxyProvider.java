package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

@ApplicationScoped
public class ActionProxyProvider implements ProxyProvider<Action> {
    private static final Logger logger = LoggerFactory.getLogger(ActionProxyProvider.class);
    private final Compiler compiler;
    private final JsonCodec jsonCodec;

    @Inject
    public ActionProxyProvider(Compiler compiler,
                               JsonCodec jsonCodec) {
        this.compiler = compiler;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Action provide(String json) {
        return null;
    }
}
