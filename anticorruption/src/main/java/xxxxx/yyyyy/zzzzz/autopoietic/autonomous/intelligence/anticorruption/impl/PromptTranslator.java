package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;

@ApplicationScoped
public class PromptTranslator implements Translator<String, String> {
    @Override
    public String toInternal(String id, String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toExternal(String id, String st) {
        throw new UnsupportedOperationException();
    }
}
