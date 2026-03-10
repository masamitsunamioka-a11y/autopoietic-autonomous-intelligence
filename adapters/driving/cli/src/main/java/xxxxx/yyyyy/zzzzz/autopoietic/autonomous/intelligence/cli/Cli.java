package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

import java.util.Iterator;
import java.util.Scanner;

public class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);
    private final Iterable<String> inputSource;
    private final boolean isInteractive;
    private final WeldContainer container;
    private final Thalamus thalamus;
    private final Salience salience;
    private final Episode episode;

    public static void main(String[] args) {
        new Cli().launch();
    }

    public Cli() {
        this(new DefaultScannerSource(), true);
    }

    public Cli(Iterable<String> inputSource, boolean isInteractive) {
        this.inputSource = inputSource;
        this.isInteractive = isInteractive;
        this.container = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize();
        this.thalamus = this.container.select(Thalamus.class).get();
        this.salience = this.container.select(Salience.class).get();
        this.episode = this.container.select(Episode.class).get();
        /// Force @PostConstruct by resolving the client proxy via no-op toString()
        this.container.select(Default.class).get().toString();
        this.container.select(Sleep.class).get().toString();
    }

    public void launch() {
        try {
            this.interact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.container.close();
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
            this.episode.encode(new TraceImpl("user", input));
            this.salience.orient();
            try {
                this.thalamus.relay(new ImpulseImpl(input, null));
                this.salience.await();
            } catch (Exception e) {
                logger.error("[CLI] respond failed", e);
                if (!this.isInteractive) {
                    throw e;
                }
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
