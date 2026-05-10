package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/nomad")
public class NomadController {

    private static final Logger LOG = LogManager.getLogger(NomadController.class);

    private final NomadService nomadService;

    public NomadController(NomadService nomadService) {
        this.nomadService = nomadService;
    }

    @Get("/jobs")
    Flux<NomadJobDTO> getNomadJobsFromProjectName(HttpRequest<?> httpRequest, @QueryValue String projectName){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        var response = nomadService.getFromProjectName(projectName);
        return response.map(NomadJobDTO::from);
    }
}
