package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;

import java.util.concurrent.SubmissionPublisher;

@ApplicationScoped
public class PublisherImpl extends SubmissionPublisher<Event> implements Publisher {
    private static final Logger logger = LoggerFactory.getLogger(PublisherImpl.class);
}
