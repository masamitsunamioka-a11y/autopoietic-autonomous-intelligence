package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;

@ApplicationScoped
public class TransducerImpl implements Transducer {
    private static final Logger logger = LoggerFactory.getLogger(TransducerImpl.class);

    @Inject
    public TransducerImpl() {
    }

    @Override
    public Impulse transduce(Stimulus stimulus) {
        return Impulse.of(stimulus.input(), null);
    }
}
