package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Command;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.CommandPublisher;

@ApplicationScoped
public class CommandPublisherImpl implements CommandPublisher {
    private static final Logger logger = LoggerFactory.getLogger(CommandPublisherImpl.class);
    private final BeanManager beanManager;

    @Inject
    public CommandPublisherImpl(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public <T extends Command> void publish(T command) {
        this.beanManager.getEvent().fire(command);
    }
}
