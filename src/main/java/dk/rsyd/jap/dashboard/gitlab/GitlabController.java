package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Flux;

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

    @Get("/commitsFromMaster/{projectId}")
    Flux<Commit> getMasterCommits(int projectId){
        return gitlabService.getMasterCommits(projectId);
    }

}