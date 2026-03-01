package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import com.google.genai.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;

/// https://github.com/googleapis/java-genai/blob/main/examples/src/main/java/com/google/genai/examples/GenerateContent.java
@ApplicationScoped
public class GeminiService implements Service<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final String MODEL = "gemini-2.0-flash";

    @Inject
    public GeminiService() {
    }

    @Override
    public String call(String input) {
        /// Client instance should also be provided via a CDI Provider.
        try (var client = new Client()) {
            return client.models
                .generateContent(MODEL, input, null).text();
        }
    }
}
