package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working;

import java.time.Instant;

/// One memory trace — the fundamental unit of memory encoding (Kandel Ch.63-67).
public interface Trace {
    String key();

    Object value();

    /// [Engineering] Explicit timestamp for chronological retrieval; biology infers temporality from decay/context.
    Instant timestamp();

    /// [Engineering] Encoded identity — key + temporal marker.
    default String encoded() {
        return this.key() + "@" + this.timestamp();
    }

    static Trace of(String key, Object value) {
        return of(key, value, Instant.now());
    }

    static Trace of(String key, Object value, Instant timestamp) {
        record Instance(String key, Object value, Instant timestamp) implements Trace {
        }
        return new Instance(key, value, timestamp);
    }

    /// [Engineering] Reconstruct from encoded identity (key@timestamp).
    static Trace decode(String encoded, Object value) {
        var at = encoded.lastIndexOf('@');
        if (at < 0) {
            return of(encoded, value);
        }
        return of(
            encoded.substring(0, at),
            value,
            Instant.parse(encoded.substring(at + 1)));
    }
}
