package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;

@ApplicationScoped
public class Startup {
    private static final Logger logger = LoggerFactory.getLogger(Startup.class);
    private final Default defaultMode;
    private final Sleep sleep;
    private final Monitor monitor;

    @Inject
    public Startup(Default defaultMode, Sleep sleep, Monitor monitor) {
        this.defaultMode = defaultMode;
        this.sleep = sleep;
        this.monitor = monitor;
    }

    public void onStartup(
        @Observes @Initialized(ApplicationScoped.class)
        Object event) {
        this.defaultMode.toString();
        this.sleep.toString();
        this.monitor.toString();
    }
}
