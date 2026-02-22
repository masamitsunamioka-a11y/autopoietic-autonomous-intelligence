package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

@ApplicationScoped
public class ActionTranslator implements Translator<Action, String> {
    private static final Logger logger = LoggerFactory.getLogger(ActionTranslator.class);
    private final String actionsPackage;

    public ActionTranslator() {
        var configuration = new Configuration("anticorruption.yaml");
        this.actionsPackage = configuration.get("anticorruption.actions.package");
    }

    @Override
    public Action translateFrom(String id, String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String translateTo(String id, Action action) {
        return """
            package %s;
            import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;
            import java.util.Map;
            public class %s implements Action {
                @Override public String name() { return "%s"; }
                @Override public String label() { return "%s"; }
                @Override public String description() { return "%s"; }
                @Override
                public Map<String, Object> execute(Map<String, Object> input) {
                    return Map.of(
                            "message", "Action [%s] successfully executed."
                    );
                }
            }
            """
            .formatted(this.actionsPackage, id, id, action.label(), action.description(), id);
    }
}
