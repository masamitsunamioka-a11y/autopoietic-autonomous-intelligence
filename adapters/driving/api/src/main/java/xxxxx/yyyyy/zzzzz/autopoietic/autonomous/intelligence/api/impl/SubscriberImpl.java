package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Events;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Subscriber;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@ApplicationScoped
public class SubscriberImpl implements Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberImpl.class);
    private final Events events;

    @Inject
    public SubscriberImpl(Events events) {
        this.events = events;
    }

    @Override
    public void onStimulus(@Observes Stimulus stimulus) {
        this.events.queue(new EventImpl(
            "user",
            "user",
            stimulus.energy().toString()));
    }

    @Override
    public void onPercept(@Observes Percept percept) {
        this.events.queue(new EventImpl(
            "message",
            percept.location(),
            percept.content()));
    }
}
