package dk.rsyd.jap.dashboard.project;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/project")
public class ProjectController {

    private static final Logger LOG = LogManager.getLogger(ProjectController.class);

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Get()
    Flux<ProjectPreview> getProjectPreviews(HttpRequest<?> httpRequest, @QueryValue int perPage, @QueryValue(defaultValue = "1") int page){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return projectService.getProjectPreviews(perPage, page);
    }
}
