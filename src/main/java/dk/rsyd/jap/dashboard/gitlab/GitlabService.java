package dk.rsyd.jap.dashboard.gitlab;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

@Singleton
public class GitlabService{
    private final GitlabClient gitlabApiClient;

    public GitlabService(GitlabClient gitlabApiClient) {
        this.gitlabApiClient = gitlabApiClient;
    }

    public Flux<GitlabProject> getAllProjects() {
        return gitlabApiClient.fetchProjectsWithHeaders(1)
            .flatMapMany(response -> Flux.fromIterable(response.body()));
    }


}
