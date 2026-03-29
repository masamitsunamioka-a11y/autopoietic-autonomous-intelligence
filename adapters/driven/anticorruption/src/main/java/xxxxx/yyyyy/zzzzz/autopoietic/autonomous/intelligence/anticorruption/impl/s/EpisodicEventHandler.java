package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.StrengthDecayed;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceEncoded;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.TraceRemoved;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Map;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

@ApplicationScoped
public class EpisodicEventHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(EpisodicEventHandler.class);
    private final LocalFileSystem extern;
    private final Serializer serializer;
    private final double decayFactor;

    @Inject
    public EpisodicEventHandler(Serializer serializer) {
        var configuration = new Configuration().scope("hippocampal").scope("episode");
        this.extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        this.serializer = serializer;
        this.decayFactor = Double.parseDouble(configuration.get("decay"));
    }

    public void handle(@Observes Event event) {
        switch (event) {
            case TraceEncoded x -> this.onEncode(x);
            case StrengthDecayed x -> this.onDecay(x);
            case TraceRemoved x -> this.onRemove(x);
            default -> {
            }
        }
    }

    private void onEncode(TraceEncoded event) {
        var timestamp = now(ZoneId.of("Asia/Tokyo")).format(ofPattern("yyyyMMddHHmmss"));
        var filename = timestamp + "_" + this.snakeCase(event.id()) + ".json";
        var uri = this.extern.resolve(filename);
        var json = this.serializer.serialize(Map.of("strength", 1.0, "trace", Map.of("id", event.id(), "content", event.content())));
        this.extern.put(new JsonResource(uri, json));
    }

    private void onDecay(StrengthDecayed event) {
        this.extern.set().stream()
            .filter(x -> Path.of(x).getFileName().toString().contains(this.snakeCase(event.id())))
            .forEach(x -> {
                var resource = (JsonResource) this.extern.get(x);
                var node = this.serializer.<ObjectNode>deserialize(resource.content(), ObjectNode.class);
                node.put("strength", event.newStrength());
                this.extern.put(new JsonResource(x, this.serializer.serialize(node)));
            });
    }

    private void onRemove(TraceRemoved event) {
        this.extern.set().stream()
            .filter(x -> Path.of(x).getFileName().toString().contains(this.snakeCase(event.id())))
            .forEach(this.extern::remove);
    }

    private String snakeCase(String id) {
        return id.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
    }
}
