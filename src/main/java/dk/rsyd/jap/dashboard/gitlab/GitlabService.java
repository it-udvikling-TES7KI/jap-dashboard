package dk.rsyd.jap.dashboard.gitlab;

import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Singleton
public class GitlabService {

    private static final Logger LOG = LogManager.getLogger(GitlabService.class);

    private final GitlabClient gitlabClient;

    public GitlabService(GitlabClient gitlabClient) {
        this.gitlabClient = gitlabClient;
    }

    public Flux<GitlabProject> getProjects(int perPage, int page){
        return  gitlabClient.fetchProjects(perPage, page)
            .map(GitlabProject::fromJapNameSpace);
    }

    public Mono<GitlabClientDTOs.Commit> findCommitFromLatestProdDeploy(int projectId) {
        return gitlabClient.fetchSuccessfulJobsFromProject(projectId)
            .filter(job ->
                (Objects.equals(job.name(), "deploy-prod"))
                    || (Objects.equals(job.name(), "deploy-production")))
            .next()
            .map(GitlabClientDTOs.Job::commit);
    }

    /**
     * Checks both Master and Main as reference.
     *
     * @param gitId
     * @return
     */
    public Mono<Commit> fetchCommitFromPrimaryBranch(int gitId) {
        return gitlabClient.fetchCommitsFromMasterBranch(gitId)
            .switchIfEmpty(gitlabClient.fetchCommitsFromMainBranch(gitId))
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
