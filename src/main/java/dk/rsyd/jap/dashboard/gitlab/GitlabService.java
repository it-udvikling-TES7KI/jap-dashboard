package dk.rsyd.jap.dashboard.gitlab;

import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Singleton
public class GitlabService {

    private final GitlabClient gitlabClient;

    public GitlabService(GitlabClient gitlabClient) {
        this.gitlabClient = gitlabClient;
    }

    public Mono<GitlabClientDTOs.Commit> findCommitFromLatestProdDeploy(int projectId){
        return gitlabClient.fetchSuccessfulJobsFromProject(projectId)
            .filter(job -> Objects.equals(job.name(), "deploy-production"))
            .next()
            .map(GitlabClientDTOs.Job::commit);
    }

}
