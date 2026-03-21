package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;

import java.util.Set;

@ApplicationScoped
public class LlmImpl implements Llm {
    private static final Logger logger = LoggerFactory.getLogger(LlmImpl.class);
    private static final Set<Class<?>> SONNET_CALLERS =
        Set.of(Cortex.class, Autopoiesis.class);
    private final AnthropicClient client;

    public LlmImpl() {
        this.client = AnthropicOkHttpClient.fromEnv();
    }

    @Override
    public String call(String prompt, Class<?> caller) {
        var params = MessageCreateParams.builder()
            .model(this.modelFor(caller))
            .maxTokens(8192L)
            .addUserMessage(prompt)
            .build();
        var message = this.client.messages().create(params);
        return message.content().stream()
            .flatMap(x -> x.text().stream())
            .map(TextBlock::text)
            .findFirst()
            .orElse("");
    }

    private Model modelFor(Class<?> caller) {
        return SONNET_CALLERS.contains(caller)
            ? Model.CLAUDE_SONNET_4_6
            : Model.CLAUDE_HAIKU_4_5_20251001;
    }
}
