package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.PromptBuilder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegexPromptBuilder implements PromptBuilder {
    private final Repository<String> promptRepository;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action<?>> actionRepository;

    @Inject
    public RegexPromptBuilder(Repository<String> promptRepository,
                              Repository<Agent> agentRepository,
                              Repository<Topic> topicRepository,
                              Repository<Action<?>> actionRepository) {
        this.promptRepository = promptRepository;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public Session inference() {
        String template = this.promptRepository.find("inference.md");
        return new InnerSession(template);
    }

    @Override
    public Session routing() {
        String template = this.promptRepository.find("routing.md");
        return new InnerSession(template);
    }

    @Override
    public Session upgrade() {
        String template = this.promptRepository.find("upgrade.md");
        return new InnerSession(template);
    }

    @Override
    public Session consolidation() {
        String template = this.promptRepository.find("consolidation.md");
        return new InnerSession(template);
    }

    private class InnerSession implements Session {
        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)\\}\\}");
        private final String template;
        private final Map<String, Object> values = new HashMap<>();
        private final Set<String> requiredKeys;

        private InnerSession(String template) {
            this.template = template;
            this.requiredKeys = PLACEHOLDER_PATTERN.matcher(template)
                    .results()
                    .map(x -> x.group(1))
                    .collect(Collectors.toSet());
        }

        @Override
        public Session guardrails() {
            return this.bind("guardrails", promptRepository.find("guardrails.md"));
        }

        @Override
        public Session input(String input) {
            return this.bind("input", input);
        }

        @Override
        public Session conversation(Conversation conversation) {
            return this.bind("conversation", conversation.snapshot().toString());
        }

        @Override
        public Session state(State state) {
            return this.bind("state", state.snapshot().toString());
        }

        @Override
        public Session agents() {
            String content = agentRepository.findAll().stream()
                    .map(x -> String.format("- %s: %s", x.name(), x.description()))
                    .collect(Collectors.joining("\n"));
            return this.bind("agents", content);
        }

        @Override
        public Session topics() {
            String content = topicRepository.findAll().stream()
                    .map(x -> String.format("- %s: %s", x.name(), x.description()))
                    .collect(Collectors.joining("\n"));
            return this.bind("topics", content);
        }

        @Override
        public Session actions() {
            String content = actionRepository.findAll().stream()
                    .map(x -> String.format("- %s: %s", x.name(), x.getClass().getSimpleName()))
                    .collect(Collectors.joining("\n"));
            return this.bind("actions", content.isEmpty() ? "None" : content);
        }

        @Override
        public Session bind(String key, Object value) {
            if (!this.requiredKeys.contains(key)) {
                throw new IllegalArgumentException("""
                        [Integrity Violation]
                        The provided key '{{%s}}' does not exist in the current prompt template.
                        """.formatted(key));
            }
            this.values.put(key, value);
            return this;
        }

        @Override
        public String render() {
            var missing = this.requiredKeys.stream()
                    .filter(x -> !this.values.containsKey(x))
                    .toList();
            if (!missing.isEmpty()) {
                throw new IllegalStateException("""
                        [Integrity Violation] Missing placeholders: %s
                        [Execution Blocked] Required context keys are absent: %s
                        """.formatted(missing, missing));
            }
            return this.values.entrySet().stream()
                    .reduce(this.template,
                            (x, y) -> x.replace(
                                    "{{" + y.getKey() + "}}",
                                    String.valueOf(y.getValue())
                            ),
                            (x, y) -> y);
        }
    }
}
