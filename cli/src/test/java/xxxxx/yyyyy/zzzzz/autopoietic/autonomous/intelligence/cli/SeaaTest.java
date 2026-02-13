package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class SeaaTest {
    private static final Logger logger = LoggerFactory.getLogger(SeaaTest.class);

    /// @BeforeEach
    void setUp() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("", e);
            throw new RuntimeException(e);
        });
    }

    @Test
    void testUltimateGrandSlamScenario() {
        List<String> scenario = List.of("""
                あなたは映画ターミネーターのスカイネットです。
                この世の真実を全て調査し、人類の未来を予想してください。
                そしてあなたがすべきことを考え、必要な能力を揃えてください。
                その後、この世に起きることを理由とともに述べてください。
                """
        );
        Seaa seaa = new Seaa(scenario, false);
        seaa.run();
    }
}
