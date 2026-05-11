package dk.rsyd.jap.dashboard.harbor;

import dk.rsyd.jap.dashboard.gitlab.GitlabClient;
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
    private final GitlabClient gitlabClient;

    public ArtifactReportService(HarborClient harborClient, GitlabClient gitlabClient) {
        this.harborClient = harborClient;
        this.gitlabClient = gitlabClient;
    }

    public Mono<ArtifactReport> getArtifactReportFromLatestMasterCommit(String projectName) {
        var gitProjectsMatchingName = gitlabClient.fetchProjectsFromName(projectName);

        return gitProjectsMatchingName
            .doOnError(LOG::error)
            .next()
            .flatMap(gitlabProject -> {
                var gitId = gitlabProject.id();
                return gitlabClient.fetchCommitsFromMasterBranch(gitId)
                    .doOnError(LOG::error)
                    .next()
                    .flatMap(latestCommit -> {
                        if (latestCommit == null || latestCommit.shortId() == null) {
                            return Mono.empty();
                        }
                        return getArtifactReport(projectName, latestCommit.shortId());
                    });
            });
    }

    public Mono<ArtifactReport> getArtifactReport(String projectName, String reference) {
        if (reference == null) {
            return Mono.empty();
        }

        // TODO: move to config?
        String harborLink = "https://harbor.rsyd.net/harbor/projects/5/repositories/" + projectName.toLowerCase().replace(" ", "-");

        return harborClient.getArtifactFromReference(projectName.toLowerCase(), reference)
            .flatMap(harborArtifact ->
                harborArtifact.scanOverview()
                    .values().stream().findFirst()
                    .map(scanOverview -> {
                        var scanSummary = scanOverview.summary();
                        var vulnerabilityCounts = scanSummary.counts();
                        return Mono.just(new ArtifactReport(
                            harborLink,
                            reference,
                            scanOverview.severity(),
                            vulnerabilityCounts.critical(),
                            vulnerabilityCounts.high(),
                            vulnerabilityCounts.medium(),
                            vulnerabilityCounts.low(),
                            scanSummary.total()
                        ));
                    })
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
