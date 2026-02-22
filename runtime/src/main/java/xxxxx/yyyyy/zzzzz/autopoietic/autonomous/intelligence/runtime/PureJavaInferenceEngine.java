package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PureJavaInferenceEngine implements InferenceEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaInferenceEngine.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final RoutingEngine routingEngine;
    private final EvolutionEngine evolutionEngine;
    private final Repository<Action> actionRepository;

    @Inject
    public PureJavaInferenceEngine(Intelligence intelligence,
                                   PromptAssembler promptAssembler,
                                   RoutingEngine routingEngine,
                                   EvolutionEngine evolutionEngine,
                                   Repository<Action> actionRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.routingEngine = routingEngine;
        this.evolutionEngine = evolutionEngine;
        this.actionRepository = actionRepository;
    }

    @Override
    public Inference infer(Context context) {
        var agent = this.routingEngine.route(context);
        var prompt = this.promptAssembler.inference(context, agent);
        var conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], Phase: {}, Action: {}, Handoff: {}",
            conclusion.confidence(),
            conclusion.reasoning(),
            conclusion.phase(),
            conclusion.action(),
            conclusion.handoffTo()
        );
        return switch (conclusion.phase()) {
            case "ANSWER" -> {
                yield this.answer(context.conversation(), agent, conclusion);
            }
            case "ACT" -> {
                this.act(agent, conclusion.action(), context.state());
                yield this.infer(context);
            }
            case "HANDOFF" -> {
                this.handoff(context.state(), agent, conclusion.handoffTo());
                yield this.infer(context);
            }
            case "EVOLVE" -> {
                this.evolve(context, agent);
                yield this.infer(context);
            }
            default -> throw new IllegalStateException();
        };
    }

    private Inference answer(Conversation conversation, Agent agent, Conclusion conclusion) {
        conversation.write(agent.name(), conclusion.answer());
        return new InternalInference(
            agent.name(),
            conclusion.reasoning(),
            conclusion.answer());
    }

    private static record InternalInference(
        String agent,
        String reasoning,
        String answer) implements Inference {
        @Override
        public double confidence() {
            return 1.0;
        }
    }

    private void act(Agent agent, String name, State state) {
        logger.trace("\n--- conclusion: {}\n--- own: {}\n--- all: {}",
            name,
            this.own(agent),
            this.all());
        var output = agent.topics().stream()
            .flatMap(x -> x.actions().stream())
            .filter(x -> x.name().equals(name))
            .findFirst()
            .orElseThrow()
            .execute(state.snapshot());
        output.forEach((k, v) -> {
            state.write("action_results." + name + "." + k, v);
        });
    }

    private Set<String> own(Agent agent) {
        return agent.topics().stream()
            .flatMap(x -> x.actions().stream())
            .map(Action::name)
            .collect(Collectors.toSet());
    }

    private Set<String> all() {
        return this.actionRepository.findAll().stream()
            .map(Action::name)
            .collect(Collectors.toSet());
    }

    private void handoff(State state, Agent agent, String handoffTo) {
        state.write("last_handoff_from", agent.name());
        state.write("last_handoff_to", handoffTo);
    }

    private void evolve(Context context, Agent agent) {
        this.evolutionEngine.upgrade(context, agent);
        this.evolutionEngine.consolidate();
    }
}
