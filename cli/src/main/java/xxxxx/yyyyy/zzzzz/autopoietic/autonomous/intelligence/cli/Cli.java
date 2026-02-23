package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.TypeLiteral;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ClassScannerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ProxyContainerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Memory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Thalamus;

import java.util.Iterator;
import java.util.Scanner;

public class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private final Iterable<String> inputSource;
    private final boolean isInteractive;
    private final Conversation conversation;
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final Drive drive;
    private final Memory memory;

    public static void main(String[] args) {
        new Cli().launch();
    }

    public Cli() {
        this(new DefaultScannerSource(), true);
    }

    public Cli(Iterable<String> inputSource, boolean isInteractive) {
        var classScanner = new ClassScannerImpl("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence");
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
            new TypeLiteral<Memory>() {}.type());
        /// @formatter:on
    }

    public void launch() {
        this.conversation.begin();
        try {
            if (this.isInteractive) {
                this.drive.start();
            }
            this.interact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (this.isInteractive) {
                this.drive.stop();
            }
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
            this.memory.record("user", input);
            var percept = this.cortex.perceive(
                this.thalamus.relay(new ImpulseImpl(input, null)));
            System.out.printf("%s> %s%n",
                percept.neuron(),
                percept.answer());
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
