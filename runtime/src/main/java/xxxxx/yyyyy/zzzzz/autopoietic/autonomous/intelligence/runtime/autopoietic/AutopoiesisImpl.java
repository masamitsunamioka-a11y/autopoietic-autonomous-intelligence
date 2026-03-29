package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.CommandPublisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

@ApplicationScoped
public class AutopoiesisImpl implements Autopoiesis {
    private static final Logger logger = LoggerFactory.getLogger(AutopoiesisImpl.class);
    private final Nucleus nucleus;
    private final Service<Impulse, Potential> transmitter;
    private final CommandPublisher commandPublisher;

    @Inject
    public AutopoiesisImpl(Nucleus nucleus,
                           @Releasic @Diffusic @Bindic
                           Service<Impulse, Potential> transmitter,
                           CommandPublisher commandPublisher) {
        this.nucleus = nucleus;
        this.transmitter = transmitter;
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void compensate(Impulse impulse) {
        var compensation = (Compensation) this.transmitter.call(new ImpulseImpl(impulse.signal(), this.label(), impulse.efferent()));
        this.nucleus.integrate(compensation, x -> {
            try {
                this.commandPublisher.publish(new CompensateNeural(x));
            } catch (Exception e) {
                logger.error("compensate failed", e);
            }
        });
    }

    @Override
    public void conserve() {
        var conservation = (Conservation) this.transmitter.call(new ImpulseImpl(null, this.label(), null));
        this.nucleus.integrate(conservation, x -> {
            try {
                this.commandPublisher.publish(new ConserveNeural(x));
            } catch (Exception e) {
                logger.error("conserve failed", e);
            }
        });
    }

    private String label() {
        return Autopoiesis.class.getSimpleName();
    }
}
