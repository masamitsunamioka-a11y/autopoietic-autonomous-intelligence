package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.TextResource;

import java.net.URI;

public record JsonResource(
    URI uri,
    String content
) implements TextResource {
}
