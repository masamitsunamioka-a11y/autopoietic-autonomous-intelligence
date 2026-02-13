package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.nio.charset.Charset;
import java.util.stream.Stream;

public interface FileSystem {
    String read(String path, Charset charset);

    void write(String path, String content, Charset charset);

    boolean exists(String path);

    Stream<String> list(String directoryPath);

    Stream<String> walk(String directoryPath);

    void delete(String path);
}
