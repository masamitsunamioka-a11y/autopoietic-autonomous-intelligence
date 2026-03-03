package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Storage {
    String read(Path path, Charset charset);

    void write(Path path, String content, Charset charset);

    boolean exists(Path path);

    Stream<Path> walk(Path path, boolean recursive);

    default Stream<Path> list(Path path) {
        return this.walk(path, false);
    }

    default Stream<Path> walk(Path path) {
        return this.walk(path, true);
    }

    void delete(Path path);
}
