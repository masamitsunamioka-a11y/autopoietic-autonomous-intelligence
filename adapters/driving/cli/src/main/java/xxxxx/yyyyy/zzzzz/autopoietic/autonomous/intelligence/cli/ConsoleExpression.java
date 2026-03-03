package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.Expression;

@ApplicationScoped
public class ConsoleExpression {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleExpression.class);

    public void onExpression(@Observes Expression expression) {
        var p = expression.percept();
        if (expression.vocalize()) {
            System.out.printf("%n%s>%n%s%n",
                p.location(), p.content());
        } else {
            System.out.printf("%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n",
                p.location(), p.content());
        }
    }
}
