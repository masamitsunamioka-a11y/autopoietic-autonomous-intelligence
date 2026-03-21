package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission.Dispatcher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission.Dispatcher.Pathway;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission.Llm;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission.Potentifier;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission.Promptifier;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

@Releasic
@Diffusic
@Bindic
@ApplicationScoped
public class TransmissionService implements Service<Impulse, Potential> {
    private static final Logger logger = LoggerFactory.getLogger(TransmissionService.class);
    private final Dispatcher dispatcher;
    private final Promptifier promptifier;
    private final Llm llm;
    private final Potentifier potentifier;

    @Inject
    public TransmissionService(Dispatcher dispatcher, Promptifier promptifier,
                               Llm llm, Potentifier potentifier) {
        this.dispatcher = dispatcher;
        this.promptifier = promptifier;
        this.llm = llm;
        this.potentifier = potentifier;
    }

    @Override
    public Potential call(Impulse impulse) {
        var pathway = this.dispatcher.dispatch(impulse);
        var released = this.release(pathway, impulse);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", released);
        }
        var diffused = this.diffuse(pathway, released);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", diffused);
        }
        return this.bind(pathway, diffused);
    }

    private String release(Pathway pathway, Impulse impulse) {
        return this.promptifier.promptify(
            pathway.template(), impulse);
    }

    private String diffuse(Pathway pathway, String released) {
        return this.llm.call(
            released, pathway.caller());
    }

    private Potential bind(Pathway pathway, String diffused) {
        return this.potentifier.potentify
            (diffused, pathway.response());
    }
}
