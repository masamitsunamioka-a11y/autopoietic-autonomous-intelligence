package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

import java.nio.charset.Charset;
import java.util.stream.Stream;

public interface FileSystem {
    String read(String path, Charset charset);

    void write(String path, String content, Charset charset);

    boolean exists(String path);

    Stream<String> list(String directoryPath);

    Stream<String> walk(String directoryPath);
}
