package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.Expression;

@ApplicationScoped
public class SseExpression {
    private static final Logger logger = LoggerFactory.getLogger(SseExpression.class);
    private final SseRegistry registry;

    @Inject
    public SseExpression(SseRegistry registry) {
        this.registry = registry;
    }

    public void onExpression(@Observes Expression expression) {
        var p = expression.percept();
        var type = expression.vocalize() ? "message" : "introspection";
        this.registry.broadcast(
            SseRegistry.buildJson(type, p.location(), p.content()));
    }
}
