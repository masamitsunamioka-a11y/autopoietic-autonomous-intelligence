package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.AdapterPlugin;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class TimestampAdapterPlugin implements AdapterPlugin<Trace> {
    private static final Logger logger = LoggerFactory.getLogger(TimestampAdapterPlugin.class);

    @Override
    public Trace onFetch(Trace trace) {
        var id = trace.id();
        var separator = id.indexOf('_');
        return separator > 0
            ? new TraceImpl(id.substring(separator + 1), trace.content())
            : trace;
    }

    @Override
    public Trace onPublish(Trace trace) {
        var timestamp = now().format(ofPattern("yyyyMMddHHmmss"));
        return new TraceImpl(timestamp + "_" + trace.id(), trace.content());
    }
}
