package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl.Mode.CEN;

@ApplicationScoped
public class ReceptorImpl implements Receptor {
    private static final Logger logger = LoggerFactory.getLogger(ReceptorImpl.class);
    private final Salience salience;
    private final Thalamus thalamus;
    private final Event<Stimulus> event;

    @Inject
    public ReceptorImpl(Salience salience, Thalamus thalamus,
                        Event<Stimulus> event) {
        this.salience = salience;
        this.thalamus = thalamus;
        this.event = event;
    }

    @Override
    public void transduce(Stimulus stimulus) {
        this.event.fire(stimulus);
        this.salience.orient();
        this.thalamus.relay(
            new ImpulseImpl(
                stimulus.energy(), this.getClass(), null, CEN));
        this.salience.await();
    }
}
