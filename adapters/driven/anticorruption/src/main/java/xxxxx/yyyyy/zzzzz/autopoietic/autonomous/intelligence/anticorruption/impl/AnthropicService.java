package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.integrative.Diffusic;

@Diffusic
@ApplicationScoped
public class AnthropicService implements Service<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(AnthropicService.class);

    @Override
    public String call(String signal) {
        var client = AnthropicOkHttpClient.fromEnv();
        var params = MessageCreateParams.builder()
            .model(Model.CLAUDE_SONNET_4_20250514)
            .maxTokens(8192L)
            .addUserMessage(signal)
            .build();
        var message = client.messages().create(params);
        return message.content().stream()
            .flatMap(x -> x.text().stream())
            .map(TextBlock::text)
            .findFirst()
            .orElse("");
    }
}
