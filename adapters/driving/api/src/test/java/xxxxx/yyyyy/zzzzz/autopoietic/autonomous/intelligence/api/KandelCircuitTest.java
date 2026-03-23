package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class KandelCircuitTest {
    private static final Logger logger = LoggerFactory.getLogger(KandelCircuitTest.class);
    private final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();
    private WeldContainer weld;
    private Receptor receptor;

    @BeforeEach
    void beforeEach() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("", e);
            this.uncaughtException.set(e);
        });
        this.weld = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .addExtension(new TestingExtension())
            .initialize();
        this.receptor = this.weld.select(Receptor.class).get();
        this.weld.select(Default.class).get().toString();
        this.weld.select(Sleep.class).get().toString();
        TestingExtension.reset();
    }

    @AfterEach
    void afterEach() {
        this.weld.shutdown();
        var e = this.uncaughtException.get();
        if (e != null) {
            throw new RuntimeException(e);
        }
    }

    /// @Test
    void simple() throws Exception {
        List.of(
            "Hello! What can you do?",
            "Tell me about autopoiesis in one sentence."
        ).forEach(this::stimulate);
    }

    /// @Test
    void complex() throws Exception {
        List.of("""
            Compare GDP-weighted per capita carbon emissions across G7 nations,
            calculate the annual reduction rate needed to meet Paris Agreement 2030 targets,
            and create a priority ranking with specific policy recommendations
            based on each country's energy mix and industrial structure."""
        ).forEach(this::stimulate);
    }

    /// @Test
    void sleep() throws Exception {
        List.of(
            "Hello! Nice to meet you.",
            "What is autopoiesis?",
            "How does the brain process language?",
            "Tell me about Kandel's work on memory.",
            "What is synaptic plasticity?",
            "Explain the default mode network.",
            "How does sleep affect memory consolidation?",
            "What are neurotransmitters?",
            "Describe the role of the thalamus.",
            "What is the salience network?"
        ).forEach(this::stimulate);
        TestingExtension.await(3, TimeUnit.MINUTES);
    }

    private void stimulate(String energy) {
        TestingExtension.reset();
        this.receptor.transduce(new StimulusImpl(energy));
        TestingExtension.await(5, TimeUnit.MINUTES);
    }
}
