package dk.rsyd.jap.dashboard.gitlab;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Singleton
public class GitlabService {

    private final GitlabClient gitlabClient;

    public GitlabService(GitlabClient gitlabClient) {
        this.gitlabClient = gitlabClient;
    }

    public Flux<GitlabProject> getProjects(int perPage, int page){
        return  gitlabClient.fetchProjects(perPage, page)
            .map(GitlabProject::fromJapNameSpace);
    }

    /**
     * Checks both Master and Main for commits.
     * This is because both are used as default branch, depending on the project
     * @param gitProjectId gitlab project id
     * @return latest Commit from default branch
     */
    public Mono<Commit> fetchLatestCommitFromPrimaryBranch(int gitProjectId) {
        return gitlabClient.fetchLatestCommitsFromMasterBranch(gitProjectId)
            .switchIfEmpty(gitlabClient.fetchLatestCommitsFromMainBranch(gitProjectId))
            .next()
            .map(Commit::fromDTO);
    }

    public Mono<GitlabProject> getProject(int projectId) {
        return gitlabClient.fetchProjectFromId(projectId)
            .map(GitlabProject::fromJapNameSpace);
    }

    public Mono<Commit> findCommitFromShortId(int projectId, String shortId) {
        return gitlabClient.fetchCommitFromSHA(projectId, shortId)
            .map(Commit::fromDTO);
    }
}
