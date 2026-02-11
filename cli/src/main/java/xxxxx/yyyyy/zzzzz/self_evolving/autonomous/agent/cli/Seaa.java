package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.cli;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.ClassScanner;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.ProxyContainer;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.TypeLiteral;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.impl.ClasspathClassScanner;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.impl.PureJavaProxyContainer;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Conversation;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Inference;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.ReasoningEngine;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.State;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Scanner;

public class Seaa {
    private static final Logger logger = LoggerFactory.getLogger(Seaa.class);
    private final ProxyContainer proxyContainer;
    private final Iterable<String> inputSource;
    private final boolean isInteractive;

    public static void main(String[] args) {
        new Seaa().run();
    }

    public Seaa() {
        this(new DefaultScannerSource(), true);
    }

    public Seaa(Iterable<String> inputSource, boolean isInteractive) {
        ClassScanner classScanner = new ClasspathClassScanner("xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent");
        ProxyContainer proxyContainer = new PureJavaProxyContainer(classScanner);
        this.proxyContainer = proxyContainer;
        this.inputSource = inputSource;
        this.isInteractive = isInteractive;
    }

    public void run() {
        try {
            State state = new InMemoryState();
            Conversation conversation = new InMemoryConversation();
            if (this.isInteractive) {
                System.out.print("> ");
            }
            for (String input : this.inputSource) {
                if (input == null || input.equalsIgnoreCase("exit")) break;
                conversation.write("user", input);
                String answer = this.interact(input, conversation, state);
                System.out.println(answer);
                if (this.isInteractive) {
                    System.out.print("> ");
                }
            }
        } catch (Exception e) {
            logger.error("Critical error in Seaa runtime", e);
            throw new RuntimeException(e);
        } finally {
            if (logger.isTraceEnabled()) {
                logger.trace("Final Topology: {}", this.proxyContainer.context(ApplicationScoped.class));
            }
        }
    }

    private String interact(String input, Conversation conversation, State state) {
        state.write("last_user_input", input);
        conversation.write("user", input);
        /// @formatter:off
        Type reasoningEngineType = new TypeLiteral<ReasoningEngine>() {}.type();
        /// @formatter:on
        ReasoningEngine reasoningEngine = this.proxyContainer.get(reasoningEngineType);
        Inference inferred = reasoningEngine.infer(input, conversation, state);
        return inferred.content();
    }

    private static class DefaultScannerSource implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            Scanner scanner = new Scanner(System.in);
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public String next() {
                    if (scanner.hasNextLine()) {
                        return scanner.nextLine();
                    }
                    return null;
                }
            };
        }
    }
}
