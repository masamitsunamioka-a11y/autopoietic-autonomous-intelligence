package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Observer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@ApplicationScoped
public class ObserverImpl implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(ObserverImpl.class);
    private final Publisher publisher;

    @Inject
    public ObserverImpl(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onStimulus(@Observes Stimulus stimulus) {
        this.publisher.publish(
            new UserEvent(stimulus.energy()));
    }

    @Override
    public void onPercept(@Observes Percept percept) {
        this.publisher.publish(
            new MessageEvent(percept.location(), percept.content()));
    }
}
