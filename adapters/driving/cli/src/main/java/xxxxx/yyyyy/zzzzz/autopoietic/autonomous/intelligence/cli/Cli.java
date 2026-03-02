package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;
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
        this.transducer = proxyContainer.get(Transducer.class);
        this.thalamus = proxyContainer.get(Thalamus.class);
        this.cortex = proxyContainer.get(Cortex.class);
        this.drive = proxyContainer.get(Drive.class);
        this.salience = proxyContainer.get(Salience.class);
        this.conversation = proxyContainer.get(Conversation.class);
        this.episode = proxyContainer.get(Episode.class);
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
        /// try -> for
        for (String input : this.inputSource) {
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            this.episode.encode(new TraceImpl("user", input));
            this.salience.orient();
            try {
                var percept = this.cortex.respond(
                    this.thalamus.relay(
                        this.transducer.transduce(
                            new StimulusImpl(input))));
                System.out.printf("%n%s>%n%s%n",
                    percept.location(),
                    percept.content());
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
