package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;

@Singleton
@jakarta.ejb.Startup
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

    @PostConstruct
    public void onStartup() {
        this.defaultMode.toString();
        this.sleep.toString();
        this.monitor.toString();
    }
}
