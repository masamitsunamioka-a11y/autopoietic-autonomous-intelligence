package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class InMemoryJavaFileObject extends SimpleJavaFileObject {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryJavaFileObject.class);
    private final String source;

    public InMemoryJavaFileObject(URI uri, String source) {
        super(uri, Kind.SOURCE);
        this.source = source;
    }

    @Override
    public CharSequence getCharContent(boolean ignore) {
        return this.source;
    }
}
