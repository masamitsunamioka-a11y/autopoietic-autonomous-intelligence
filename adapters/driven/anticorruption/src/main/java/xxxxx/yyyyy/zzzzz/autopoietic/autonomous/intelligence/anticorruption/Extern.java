package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.net.URI;
import java.util.Set;

public interface Extern {
    URI resolve(String name);

    Resource get(URI uri);

    void put(Resource resource);

    void remove(URI uri);

    boolean contains(URI uri);

    Set<URI> set();
}
