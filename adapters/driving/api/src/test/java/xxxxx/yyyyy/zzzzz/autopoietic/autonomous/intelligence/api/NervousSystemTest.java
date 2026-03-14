package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class NervousSystemTest {
    private static final Logger logger = LoggerFactory.getLogger(NervousSystemTest.class);
    private final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("", e);
            this.uncaughtException.set(e);
        });
    }

    @AfterEach
    void tearDown() {
        var e = this.uncaughtException.get();
        if (e != null) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void simulate() {
        try (var container = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize()) {
            var receptor = container.select(Receptor.class).get();
            container.select(Default.class).get().toString();
            container.select(Sleep.class).get().toString();
            var scenario = List.of("""
                G7各国のGDP加重一人当たり炭素排出量を比較し、
                パリ協定2030年目標達成に必要な年間削減率を算出した上で、
                各国のエネルギー構成と産業構造に基づく、
                具体的な政策提言を含む優先度ランキングを作成してください。
                """);
            for (var input : scenario) {
                receptor.transduce(new StimulusImpl(input));
            }
        }
    }
}
