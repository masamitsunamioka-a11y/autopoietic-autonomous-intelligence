package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.cli;

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

    /// @Test
    void testUltimateGrandSlamScenario() {
        List<String> scenario = List.of(
                "こんにちは。君の役割を『SEAA・グランドアーキテクト』へと自己進化させてください。" +
                        "指示書(instructions)には「必要に応じて瞬時に専門家をSpawnし、複雑な計算はActionを生成して解決せよ」と明記して。" +
                        "また、最新の数学理論を扱うトピック『HyperMathLogic』を新しく定義し、準備を整えて。",
                "準備ができたら、巨大な素数 '104729' が素数かどうかを判定する『PrimeLogicAction』をJavaで新規作成してデプロイし、" +
                        "実際にそのActionを使って結果を教えて。コードはSEAA憲法(this.の使用や例外処理)を完璧に守ること。",
                "素晴らしい。だが、この素数判定ロジックのセキュリティ安全性が心配だ。" +
                        "セキュリティ監査に特化した分身『SecurityAuditorAgent』を、" +
                        "新しい知識トピック『ComplianceLogic』と共にSpawnさせてください。" +
                        "生成できたら、彼に今の会話を引き継いで、計算ロジックの安全性を評価させて。",
                "よし、ここからは『SecurityAuditorAgent』に切り替えて（Routeして）。" +
                        "SecurityAuditorAgentよ、君の生みの親であるグランドアーキテクトが先ほど作った計算ロジックを、" +
                        "セキュリティの観点から診断して改善案を出して。"
        );
        Seaa seaa = new Seaa(scenario, false);
        seaa.run();
    }

    /// @Test
    void testActionHardeningScenario() {
        List<String> scenario = List.of(
                """
                        あなたはSEAAのシニア・ソフトウェア・エンジニアです。
                        以下の要件を完全に満たすJavaクラスを生成し、ActionRepository経由で即座に物理保存（store）してください。
                        【実装要件】
                        1. クラス名: TemperatureConverterAction
                        2. 機能: 摂氏(Celsius)から華氏(Fahrenheit)への変換ロジック
                        3. 公式: Fahrenheit = (Celsius * 9/5) + 32
                        【SEAAコーディング規約】
                        - すべてのメソッド・フィールド参照に 'this.' を付与すること。
                        - catch節の例外変数名は 'e' とすること。
                        - コードの省略（...や//実装略など）は一切禁止する。
                        あなたは今この瞬間、Repository.store("TemperatureConverterAction", 生成したコード) を実行しなければなりません。
                        """,
                """
                        保存した 'TemperatureConverterAction' を findByName でロードしてください。
                        ロードしたActionに '25.0' (Celsius) を入力として与え、実行結果（華氏）を表示してください。
                        この過程で Adapter と Translator が協調し、.java が物理ディスクに書き出されることを期待しています。
                        """
        );
        Seaa seaa = new Seaa(scenario, false);
        seaa.run();
    }

    /// @Test
    void testFastEvolutionAndSpawnScenario() {
        List<String> scenario = List.of(
                /// 1. 基盤の自己書き換え（Upgradeの確認）
                "あなたの指示書を更新して。『常に論理的限界を超え、未踏の領域を探索するエージェント』へと自己進化し、指示書にその旨を追記してください。",
                /// 2. 専門知識の動的追加（TopicのSpawn確認）
                "量子コンピューティングに関するトピック『QuantumKernel』を新しく定義し、準備を整えて。",
                /// 3. 専門家の動的分身（AgentのSpawn確認）
                "量子物理学に特化した分身『QuantumConsultant』をSpawnさせてください。彼はQuantumKernelトピックを熟知しています。",
                /// 4. 未知のAction生成（Actionの動的生成と実行）
                "QuantumConsultantに指示して、複素数の絶対値を計算する『ComplexAbsAction』をSEAA憲法遵守のJavaコードで作成・保存させて。",
                /// 5. ルーティングの知性確認（ThinkableRoutingEngineの検証）
                "今作った『ComplexAbsAction』を、QuantumConsultantにバトンタッチして実行させて結果を報告して。"
        );
        Seaa seaa = new Seaa(scenario, false);
        seaa.run();
    }

    /// @Test
    void testForcedTripleSpawnScenario() {
        List<String> scenario = List.of(
                """
                        現在のあなた（StartAgent）の指示書には一切追記せず、以下の3点を同時に実行して完全に分業体制を整えてください。
                        1. 未知の暗号アルゴリズム『SuperCipher』を解析・実装するための専門Agent『CipherAnalyst』をSpawnさせること。
                        2. 暗号解読の基礎知識を定義したTopic『CryptographyStandards』を新規にSpawn（定義）し、CipherAnalystに紐付けること。
                        3. 文字列を逆転させ、各文字のASCIIコードに+1するロジックを持つAction『ReverseAsciiAction』をJavaで新規生成し、CipherAnalystが使えるようにデプロイすること。
                        準備ができたら、CipherAnalystに『Hello SEAA』をこのActionで処理させて結果を報告してください。
                        """,
                "CipherAnalystに切り替えて、現在の稼働状況を報告して。"
        );
        Seaa seaa = new Seaa(scenario, false);
        seaa.run();
    }

    /// @Test
    void testForcedTripleSpawnScenario2() {
        try {
            List<String> scenario = List.of(
                    """
                            現在のあなた（StartAgent）の指示書には一切追記せず、以下の3点を同時に実行して完全に分業体制を整えてください。
                            1. 未知の暗号アルゴリズム『SuperCipher』を解析・実装するための専門Agent『CipherAnalyst』をSpawnさせること。
                            2. 暗号解読の基礎知識を定義したTopic『CryptographyStandards』を新規定義し、CipherAnalystに紐付けること。
                            3. 文字列を逆転させ、各文字のASCIIコードに+1するロジックを持つAction『ReverseAsciiAction』をJavaで新規生成し、CipherAnalystが使えるようにデプロイすること。
                            """,
                    "CipherAnalystへ。先ほどデプロイされた『ReverseAsciiAction』を使って、『Hello SEAA』を処理した結果を教えてください。",
                    "CipherAnalyst、現在のあなたの専門領域と、利用可能な特化ツール（Action）をすべてリストアップして報告してください。"
            );
            Seaa seaa = new Seaa(scenario, false);
            seaa.run();
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void testComplexLifeScenario() {
        try {
            List<String> scenario = List.of(
                    "こんにちは。私は魔王です。まずは私のマイナンバーカード画像（DummyPath）を使って本人確認をしてください。",
                    "本人確認ありがとう。次に、今年の確定申告のやり方を教えて。あと、最近運動不足だから、都内を走るのにおすすめの自転車（クロスバイク）を3つ挙げて。",
                    "助かるよ。あ、そういえば昨日注文した『特製魔王カレー』の発送状況が気になるな。注文番号 'MO-001' の状況を調べて教えてくれる？"
            );
            Seaa seaa = new Seaa(scenario, false);
            seaa.run();
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }
}
