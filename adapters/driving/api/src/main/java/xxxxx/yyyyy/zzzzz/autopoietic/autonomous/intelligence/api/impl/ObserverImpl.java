package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Observer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.PerceptGenerated;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

@ApplicationScoped
public class ObserverImpl implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(ObserverImpl.class);
    private final Publisher publisher;

    @Inject
    public ObserverImpl(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onPercept(@Observes Percept percept) {
        this.publisher.submit(new PerceptGenerated(percept.location(), percept.content()));
    }
}
