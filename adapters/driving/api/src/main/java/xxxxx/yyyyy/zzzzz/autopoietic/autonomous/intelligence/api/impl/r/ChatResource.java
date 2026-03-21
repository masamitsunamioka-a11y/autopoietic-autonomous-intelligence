package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.r;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import static jakarta.ws.rs.core.Response.ok;
import static jakarta.ws.rs.core.Response.serverError;

@Path("/chat")
@RequestScoped
public class ChatResource {
    private static final Logger logger = LoggerFactory.getLogger(ChatResource.class);
    private final Receptor receptor;

    public ChatResource() {
        this.receptor = null;
    }

    @Inject
    public ChatResource(Receptor receptor) {
        this.receptor = receptor;
    }

    private record Input(@NotBlank String payload) {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(@Valid @NotNull Input input) {
        try {
            this.receptor.transduce(new StimulusImpl(input.payload()));
            return ok("{}").build();
        } catch (Exception e) {
            logger.error("chat error", e);
            return serverError().build();
        }
    }
}
