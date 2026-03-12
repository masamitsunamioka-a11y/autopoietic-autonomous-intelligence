package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.TransmitterImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;

import java.util.Set;

@Diffusic
@ApplicationScoped
public class AnthropicService implements Service<TransmitterImpl.Input, String> {
    private static final Logger logger = LoggerFactory.getLogger(AnthropicService.class);
    private static final Set<Class<?>> SONNET_CALLERS =
        Set.of(Cortex.class, Autopoiesis.class);

    @Override
    public String call(TransmitterImpl.Input input) {
        var client = AnthropicOkHttpClient.fromEnv();
        var params = MessageCreateParams.builder()
            .model(this.modelFor(input.caller()))
            .maxTokens(8192L)
            .addUserMessage(input.prompt())
            .build();
        var message = client.messages().create(params);
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
