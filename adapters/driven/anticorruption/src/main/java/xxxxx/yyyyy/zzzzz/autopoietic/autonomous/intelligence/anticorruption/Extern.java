package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;

public interface Extern {
    URI resolve(String name);

    URI resolve(Path path);

    Resource get(URI uri);

    void put(URI uri, Resource resource);

    void remove(URI uri);

    boolean contains(URI uri);

    Set<URI> set();
}
