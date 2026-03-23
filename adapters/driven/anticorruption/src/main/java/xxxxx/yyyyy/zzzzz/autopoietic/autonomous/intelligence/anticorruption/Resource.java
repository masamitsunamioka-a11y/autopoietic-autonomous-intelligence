package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.net.URI;

public sealed interface Resource
    permits TextResource, BinaryResource {
    URI uri();
}
