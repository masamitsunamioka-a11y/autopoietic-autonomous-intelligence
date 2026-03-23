package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.r;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;

import static jakarta.ws.rs.core.Response.ok;

@Path("/monitor")
@RequestScoped
public class MonitorResource {
    private static final Logger logger = LoggerFactory.getLogger(MonitorResource.class);
    private final Monitor monitor;

    public MonitorResource() {
        this.monitor = null;
    }

    @Inject
    public MonitorResource(Monitor monitor) {
        this.monitor = monitor;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response snapshots() {
        return ok(this.monitor.snapshots()).build();
    }
}
