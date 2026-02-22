package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.TypeLiteral;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ClasspathClassScanner;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.PureJavaProxyContainer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Inference;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.InferenceEngine;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.State;

import java.util.Iterator;
import java.util.Scanner;

public class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private final ProxyContainer proxyContainer;
    private final Iterable<String> inputSource;
    private final boolean isInteractive;

    public static void main(String[] args) {
        new Cli().run();
    }

    public Cli() {
        this(new DefaultScannerSource(), true);
    }

    public Cli(Iterable<String> inputSource, boolean isInteractive) {
        var classScanner = new ClasspathClassScanner("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence");
        this.proxyContainer = new PureJavaProxyContainer(classScanner);
        this.inputSource = inputSource;
        this.isInteractive = isInteractive;
    }

    public void run() {
        try {
            var conversation = new InMemoryConversation();
            var state = new InMemoryState();
            if (this.isInteractive) {
                System.out.print("> ");
            }
            for (String input : this.inputSource) {
                if (input == null || input.equalsIgnoreCase("exit")) break;
                Inference inference = this.interact(input, conversation, state);
                if (this.isInteractive) {
                    System.out.printf("%s> %s\n[confidence %s, reasoning %s]\n\n",
                        inference.agent(),
                        inference.answer(),
                        inference.confidence(),
                        inference.reasoning());
                    System.out.print("> ");
                } else {
                    logger.info("Input: {}, Answer: {}", input, inference.answer());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Inference interact(String input, Conversation conversation, State state) {
        conversation.write("user", input);
        /// @formatter:off
        var inferenceEngineType = new TypeLiteral<InferenceEngine>() {}.type();
        /// @formatter:on
        InferenceEngine inferenceEngine = this.proxyContainer.get(inferenceEngineType);
        return inferenceEngine.infer(new InMemoryContext(input, conversation, state));
    }

    private static class DefaultScannerSource implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            var scanner = new Scanner(System.in);
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
