package dk.rsyd.jap.dashboard.api;

import dk.rsyd.jap.dashboard.gitlab.GitlabProject;
import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/gitlab")
public class GitlabController {

    private final GitlabService gitlabService;

    public GitlabController(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }

    @Get
    Flux<GitlabProject> getGitlabProjects(){
        return gitlabService.getAllProjects(50, 1);
    }

}