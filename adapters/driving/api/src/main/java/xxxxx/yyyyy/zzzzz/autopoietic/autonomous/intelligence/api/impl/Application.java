package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.ws.rs.ApplicationPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("/api")
public class Application extends jakarta.ws.rs.core.Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
}
