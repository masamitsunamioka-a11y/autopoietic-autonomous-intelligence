package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/// kept for super() constructor
/// Wraps System.out and parses Drive output into SSE broadcast events.
///
/// Drive emits two formats via System.out.printf:
///   vocalize:      "%n%s>%n%s%n"                        location, content
///   introspection: "%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n"  location, content
///
/// State machine:
///   IDLE    → line ending with ">" detected  → save location, go to CONTENT
///   CONTENT → next non-empty line = content  → broadcast JSON, go to IDLE
class SsePrintStream extends PrintStream {
    private static final Logger logger = LoggerFactory.getLogger(SsePrintStream.class);
    private static final String ANSI_GRAY = "\u001B[90m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String INTRO_TAG = "[introspection] ";

    private enum State {IDLE, CONTENT}

    private final SseRegistry registry;
    private final StringBuilder lineBuffer = new StringBuilder();
    private State state = State.IDLE;
    private String pendingLocation;
    private boolean pendingIsIntrospection;

    SsePrintStream(SseRegistry registry, PrintStream delegate) {
        super(delegate, true, StandardCharsets.UTF_8);
        this.registry = registry;
    }

    /// printf() → Formatter → Appendable.append() → print(String) is the actual
    /// code path in Java 25. Overriding write(byte[]) does NOT intercept it.
    @Override
    public void print(String s) {
        super.print(s);
        if (s != null) this.ingest(s);
    }

    private synchronized void ingest(String chunk) {
        for (var ch : chunk.toCharArray()) {
            if (ch == '\n') {
                this.handleLine(this.lineBuffer.toString());
                this.lineBuffer.setLength(0);
            } else if (ch != '\r') {
                this.lineBuffer.append(ch);
            }
        }
    }

    private void handleLine(String raw) {
        /// trim() removes ESC (codepoint 27 < 32) — use strip() to preserve ANSI prefixes.
        var line = raw.strip();
        if (line.isEmpty()) return;
        /// In CONTENT state, if another location header arrives (Drive fired before
        /// the previous content line was seen), reset to IDLE so it is re-processed.
        if (this.state == State.CONTENT && isLocationHeader(line)) {
            this.state = State.IDLE;
        }
        if (this.state == State.IDLE) {
            if (!isLocationHeader(line)) return;
            var isIntro = line.startsWith(ANSI_GRAY);
            var clean = line.replace(ANSI_GRAY, "").replace(ANSI_RESET, "").trim();
            var loc = clean.startsWith(INTRO_TAG)
                ? clean.substring(INTRO_TAG.length())
                : clean;
            this.pendingLocation = loc.substring(0, loc.length() - 1).trim();
            this.pendingIsIntrospection = isIntro;
            this.state = State.CONTENT;
        } else {
            var content = line.replace(ANSI_RESET, "").trim();
            var type = this.pendingIsIntrospection ? "introspection" : "message";
            var location = this.pendingLocation;
            /// Reset before broadcast to prevent reentrancy if write() is called
            /// again during SSE emission.
            this.state = State.IDLE;
            this.registry.broadcast(buildJson(type, location, content));
            logger.debug("SSE broadcast type={} location={}", type, location);
        }
    }

    /// A location header is either an introspection line (starts with ANSI gray)
    /// or a bare "AreaName>" with no spaces (CamelCase area name only).
    private static boolean isLocationHeader(String line) {
        if (line.startsWith(ANSI_GRAY)) return true;
        var clean = line.replace(ANSI_RESET, "").trim();
        if (!clean.endsWith(">")) return false;
        var candidate = clean.substring(0, clean.length() - 1);
        return !candidate.isEmpty() && !candidate.contains(" ");
    }

    static String buildJson(String type, String location, String content) {
        return "{\"type\":\"" + esc(type) + "\","
            + "\"location\":\"" + esc(location) + "\","
            + "\"content\":\"" + esc(content) + "\"}";
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
