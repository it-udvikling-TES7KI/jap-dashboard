package dk.rsyd.jap.dashboard.gitlab;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

@Singleton
public class GitlabService{
    private final GitlabClient gitlabApiClient;

    public GitlabService(GitlabClient gitlabApiClient) {
        this.gitlabApiClient = gitlabApiClient;
    }

    public Flux<GitlabProject> getAllProjects(int perPage, int page) {
        return gitlabApiClient.fetchProjectsWithHeaders(perPage, page)
            .flatMapMany(response -> Flux.fromIterable(response.body()));
    }


}
