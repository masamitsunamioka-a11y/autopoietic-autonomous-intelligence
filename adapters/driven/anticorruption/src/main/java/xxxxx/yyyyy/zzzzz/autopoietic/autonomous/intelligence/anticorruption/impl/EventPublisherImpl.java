package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventPublisher;

@ApplicationScoped
public class EventPublisherImpl implements EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherImpl.class);
    private final BeanManager beanManager;

    @Inject
    public EventPublisherImpl(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public <T extends Event> void publish(T event) {
        this.beanManager.getEvent().fire(event);
    }
}
