package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@ApplicationScoped
public class ReceptorImpl implements Receptor {
    private static final Logger logger = LoggerFactory.getLogger(ReceptorImpl.class);
    private final Salience salience;
    private final Thalamus thalamus;

    @Inject
    public ReceptorImpl(Salience salience, Thalamus thalamus) {
        this.salience = salience;
        this.thalamus = thalamus;
    }

    @Override
    public void transduce(Stimulus stimulus) {
        this.salience.orient();
        this.thalamus.relay(new ImpulseImpl(
            stimulus.energy(), this.label(), null));
    }

    private String label() {
        return Receptor.class.getSimpleName();
    }
}
