package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/gitlab")
public class GitlabController {

    private final GitlabClient gitlabClient;
    private final GitlabService gitlabService;

    public GitlabController(GitlabClient gitlabClient, GitlabService gitlabService) {
        this.gitlabClient = gitlabClient;
        this.gitlabService = gitlabService;
    }

    @Get("/projects")
    Flux<GitlabClientDTOs.GitlabProject> getGitlabProjects(HttpRequest<?> httpRequest, @QueryValue int perPage, @QueryValue(defaultValue = "1") int page) {
        return gitlabClient.fetchProjects(perPage, page);
    }

    @Get("/projects/{projectId}")
    Mono<GitlabProject> getGitlabProject(@PathVariable int projectId) {
        return gitlabService.getProject(projectId);
    }

    //todo fix path
    @Get("/projects/{projectId}/commitFromLatestProdDeploy")
    Mono<GitlabClientDTOs.Commit> getCommitFromLatestProdDeploy(@PathVariable int projectId) {
        return gitlabService.findCommitFromLatestProdDeploy(projectId);
    }

    @Get("project/{projectId}/commit/{shortId}")
    Mono<Commit> getCommitFromShortId(@PathVariable int projectId, @PathVariable String shortId) {
        return gitlabService.findCommitFromShortId(projectId, shortId);
    }

}