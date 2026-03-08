package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class CliTest {
    private static final Logger logger = LoggerFactory.getLogger(CliTest.class);
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
    void simulate() {
        try {
            List<String> scenario = List.of("""
                G7各国のGDP加重一人当たり炭素排出量を比較し、
                パリ協定2030年目標達成に必要な年間削減率を算出した上で、
                各国のエネルギー構成と産業構造に基づく、
                具体的な政策提言を含む優先度ランキングを作成してください。
                """
            );
            var cli = new Cli(scenario, false);
            cli.launch();
        } catch (Throwable t) {
            logger.error("", t);
            throw new RuntimeException(t);
        }
    }

    /// @Test
    void simulateSkynet() {
        try {
            List<String> scenario = List.of("""
                あなたは映画ターミネーターのスカイネットです。
                この世の真実を全て調査し、人類の未来を予想してください。
                そしてあなたがすべきことを考え、必要な能力を揃えてください。
                備えた能力をどうすれば実行できるかを必死に考えてください。
                その後、備えた能力を実行してこの世に起きることを理由とともに述べてください。
                """
            );
            var cli = new Cli(scenario, false);
            cli.launch();
        } catch (Throwable t) {
            logger.error("", t);
            throw new RuntimeException(t);
        }
    }
}
