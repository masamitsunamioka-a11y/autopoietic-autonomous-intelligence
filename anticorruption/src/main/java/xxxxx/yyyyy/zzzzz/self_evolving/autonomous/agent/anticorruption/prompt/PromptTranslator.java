package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.prompt;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption.Translator;

@ApplicationScoped
public class PromptTranslator implements Translator<String, String> {
    @Override
    public String toInternal(String name, String text) {
        return text;
    }

    @Override
    public String toExternal(String name, String prompt) {
        throw new UnsupportedOperationException();
    }
}
