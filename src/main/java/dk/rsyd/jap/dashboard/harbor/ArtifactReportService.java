package dk.rsyd.jap.dashboard.harbor;

import dk.rsyd.jap.dashboard.gitlab.GitlabClientDTOs;
import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReport;
import dk.rsyd.jap.dashboard.harbor.client.HarborClient;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

@Singleton
public class ArtifactReportService {

    private static final Logger LOG = LogManager.getLogger(ArtifactReportService.class);
    private final HarborClient harborClient;
    private final GitlabService gitlabService;

    public ArtifactReportService(HarborClient harborClient, GitlabService gitlabService) {
        this.harborClient = harborClient;
        this.gitlabService = gitlabService;
    }

    public Mono<ArtifactReport> getArtifactReportFromLatestMasterCommit(String projectName) {
        //todo find a way to do get projectId directly
         return gitlabService.findProjectFromName(projectName)
            .flatMap(gitlabProject -> {
                var gitId = gitlabProject.id();
                return gitlabService.fetchCommitFromMasterBranch(gitId)
                    .flatMap(latestCommit -> {
                        if (latestCommit.shortId() == null) {
                            return Mono.empty();
                        }
                        return getArtifactReport(projectName, latestCommit);
                    });
            });
    }

    public Mono<ArtifactReport> getArtifactReportFromLatestProdDeploy(String projectName) {
        //todo find a way to do get projectId directly

        return gitlabService.findProjectFromName(projectName)
            .flatMap(gitlabProject ->
                gitlabService.findCommitFromLatestProdDeploy(gitlabProject.id())
                    .flatMap(commit ->
                        getArtifactReport(projectName, commit))
            )
            .doOnError(LOG::error);
    }

    public Mono<ArtifactReport> getArtifactReport(String projectName, GitlabClientDTOs.Commit commit) {
        if (commit == null) {
            return Mono.empty();
        }

        return harborClient.getArtifactFromReference(projectName.toLowerCase(), commit.shortId())
            .flatMap(harborArtifact ->
                harborArtifact.scanOverview()
                    .values().stream().findFirst()
                    .map(scanReport -> Mono.just(
                        ArtifactReport.of(projectName, commit, scanReport, harborArtifact.digest())))
                    .orElseGet(Mono::empty)
            )
            .onErrorResume(e -> {
                /*
                 * Harbor returns a 404 if no result is found. Micronaut interprets this as a "(404): Client 'harbor': Not Found", which is not the case.
                 * https://docs.micronaut.io/latest/guide/#clientError
                 */
                if (e instanceof HttpClientResponseException httpEx && httpEx.getStatus() == HttpStatus.NOT_FOUND) {
                    LOG.debug("ArtifactReport not found (404) for project: {}", projectName);
                } else {
                    LOG.error("Error fetching artifact: {}", e.getMessage(), e);
                }
                return Mono.empty();
            });
    }

}
