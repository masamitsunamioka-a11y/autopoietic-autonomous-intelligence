package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;

@ApplicationScoped
public class PromptTranslator implements Translator<String, String> {
    @Override
    public String toInternal(String name, String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toExternal(String name, String prompt) {
        throw new UnsupportedOperationException();
    }
}
