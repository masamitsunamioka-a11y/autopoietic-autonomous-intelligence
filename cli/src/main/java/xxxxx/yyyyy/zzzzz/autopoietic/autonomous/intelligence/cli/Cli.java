package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.TypeLiteral;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ClassScannerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ProxyContainerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private final Iterable<String> inputSource;
    private final boolean isInteractive;
    private final Conversation conversation;
    private final Transducer transducer;
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final Drive drive;
    private final Salience salience;
    private final Episode episode;

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
        this.transducer = proxyContainer.get(
            new TypeLiteral<Transducer>() {}.type());
        this.thalamus = proxyContainer.get(
            new TypeLiteral<Thalamus>() {}.type());
        this.cortex = proxyContainer.get(
            new TypeLiteral<Cortex>() {}.type());
        this.drive = proxyContainer.get(
            new TypeLiteral<Drive>() {}.type());
        this.salience = proxyContainer.get(
            new TypeLiteral<Salience>() {}.type());
        this.conversation = proxyContainer.get(
            new TypeLiteral<Conversation>() {}.type());
        this.episode = proxyContainer.get(
            new TypeLiteral<Episode>() {}.type());
        /// @formatter:on
    }

    public void launch() {
        this.conversation.begin();
        try {
            this.drive.activate();
            this.interact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.drive.deactivate();
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
            this.episode.encode("user", input);
            this.salience.orient();
            try {
                var stimulus = new StimulusImpl(input);
                var impulse = this.transducer.transduce(stimulus);
                var routed = this.thalamus.relay(impulse);
                var percept = this.cortex.respond(routed);
                System.out.printf("%n%s>%n%s%n",
                    percept.location(),
                    percept.content());
                logger.info("[{}] {}", percept.location(), percept.content());
            } catch (Exception e) {
                logger.error("[CLI] respond failed", e);
            } finally {
                this.salience.release();
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
