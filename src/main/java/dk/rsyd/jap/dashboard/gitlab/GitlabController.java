package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/gitlab")
public class GitlabController {

    private static final Logger LOG = LogManager.getLogger(GitlabController.class);

    private final GitlabService gitlabService;

    public GitlabController(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }

    @Get("/projects")
    Flux<GitlabProject> getGitlabProjects(HttpRequest<?> httpRequest, @QueryValue int perPage, @QueryValue(defaultValue = "1") int page) {
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());
        return gitlabService.getProjects(perPage, page)
            .doOnError(e -> LOG.error("Error fetching projects: {}", e.getMessage()))
            .doOnComplete(() -> LOG.info("Completed fetching projects"));
    }

    @Get("/projects/{projectId}")
    Mono<GitlabProject> getGitlabProject(HttpRequest<?> httpRequest, @PathVariable int projectId) {
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());
        return gitlabService.getProject(projectId)
            .doOnError(e -> LOG.error("Error fetching project id={}: {}", projectId, e.getMessage()))
            .doOnSuccess(project -> LOG.info("Returning project: id={}, name={}", project.id(), project.name()));
    }

    @Get("project/{projectId}/commit/{shortId}")
    Mono<Commit> getCommitFromShortId(HttpRequest<?> httpRequest, @PathVariable int projectId, @PathVariable String shortId) {
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());
        return gitlabService.findCommitFromShortId(projectId, shortId)
            .doOnError(e -> LOG.error("Error fetching commit shortId={} for projectId={}: {}", shortId, projectId, e.getMessage()))
            .doOnSuccess(commit -> LOG.info("Returning commit: id={}", commit.id()));
    }

}