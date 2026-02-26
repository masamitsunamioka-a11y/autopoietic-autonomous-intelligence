package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.TypeLiteral;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ClassScannerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ProxyContainerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private final Iterable<String> inputSource;
    private final boolean isInteractive;
    private final Conversation conversation;
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final Drive drive;
    private final xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation memory;

    public static void main(String[] args) {
        new Cli().launch();
    }

    public Cli() {
        this(new DefaultScannerSource(), true);
    }

    public Cli(Iterable<String> inputSource, boolean isInteractive) {
        var classScanner = new ClassScannerImpl(
            List.of("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence"),
            List.of("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.effectors"));
        var proxyContainer = new ProxyContainerImpl(classScanner);
        this.inputSource = inputSource;
        this.isInteractive = isInteractive;
        /// @formatter:off
        this.thalamus = proxyContainer.get(
            new TypeLiteral<Thalamus>() {}.type());
        this.cortex = proxyContainer.get(
            new TypeLiteral<Cortex>() {}.type());
        this.drive = proxyContainer.get(
            new TypeLiteral<Drive>() {}.type());
        this.conversation = proxyContainer.get(
            new TypeLiteral<Conversation>() {}.type());
        this.memory = proxyContainer.get(
            new TypeLiteral<xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation>() {}.type());
        /// @formatter:on
    }

    public void launch() {
        this.conversation.begin();
        try {
            this.drive.start();
            this.interact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.drive.stop();
            this.conversation.end();
        }
    }

    private void interact() {
        if (this.isInteractive) {
            System.out.print("> ");
        }
        for (String input : this.inputSource) {
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            this.memory.encode("user", input);
            try {
                var percept = this.cortex.perceive(
                    this.thalamus.relay(
                        new StimulusImpl(input, null)));
                System.out.printf("%n%s>%n%s%n",
                    percept.neuron(),
                    percept.answer());
            } catch (Exception e) {
                logger.error("[CLI] perceive failed", e);
            }
            if (this.isInteractive) {
                System.out.print("> ");
            }
        }
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
