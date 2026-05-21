package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
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

    //todo path
    @Get
    Flux<GitlabClientDTOs.GitlabProject> getGitlabProjects() {
        return gitlabClient.fetchProjects(50, 1);
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