package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class PromptifierImpl implements Promptifier {
    private static final Logger logger = LoggerFactory.getLogger(PromptifierImpl.class);
    private static final Pattern VARIABLE = Pattern.compile("\\{\\{(\\w+)}}");
    private static final Set<String> AUTOPOIESIS =
        Set.of("compensation.md", "conservation.md");
    private final Objectifier objectifier;
    private final Stringifier stringifier;
    private final Extern functionalExtern;

    @Inject
    public PromptifierImpl(Objectifier objectifier, Stringifier stringifier) {
        this.objectifier = objectifier;
        this.stringifier = stringifier;
        var configuration = new Configuration();
        this.functionalExtern =
            new LocalFileSystem(
                Path.of(configuration.synaptic().function().get("source"), ""));
    }

    @Override
    public String promptify(String template, Impulse impulse) {
        var function = this.function(template);
        var bindings = this.keys(function).stream()
            .collect(Collectors.toMap(
                k -> k,
                k -> this.stringifier.stringify(
                    this.objectifier.objectify(k, impulse),
                    AUTOPOIESIS.contains(template)),
                (x, y) -> y));
        return this.bind(function, bindings);
    }

    private String function(String name) {
        return this.functionalExtern.get(
            this.functionalExtern.resolve(name)).content();
    }

    private List<String> keys(String template) {
        return VARIABLE.matcher(template).results()
            .map(m -> m.group(1))
            .toList();
    }

    private String bind(String function, Map<String, String> bindings) {
        return bindings.entrySet().stream()
            .reduce(function,
                (x, y) -> x.replace(
                    "{{" + y.getKey() + "}}", y.getValue()),
                (x, y) -> x);
    }
}
