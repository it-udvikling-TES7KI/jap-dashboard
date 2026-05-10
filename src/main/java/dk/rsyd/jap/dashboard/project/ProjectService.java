package dk.rsyd.jap.dashboard.project;

import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReport;
import dk.rsyd.jap.dashboard.harbor.client.HarborClient;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Singleton
public class ProjectService {

    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final GitlabService gitlabService;
    private final HarborClient harborClient;

    public ProjectService(GitlabService gitlabService, HarborClient harborClient) {
        this.gitlabService = gitlabService;
        this.harborClient = harborClient;
    }

    public Flux<ProjectPreview> getProjectPreviews(int perPage, int page) {
        return gitlabService.getAllProjects(perPage, page)
            .flatMap(gitlabProject ->
                gitlabService.getMasterCommits(gitlabProject.id())
                    .next()
                    .flatMap(latestCommit -> {
                        if (latestCommit == null || latestCommit.shortId() == null) {
                            return Mono.just(new ProjectPreview(gitlabProject, null));
                        }

                        return harborClient.getArtifactFromReference(gitlabProject.name().toLowerCase(), latestCommit.shortId())
                            .map(harborArtifact -> {
                                var scanOverviewOpt = harborArtifact.scanOverview();
                                if (scanOverviewOpt.isEmpty()) {
                                    return new ProjectPreview(gitlabProject, null);
                                }
                                var scanOverview= scanOverviewOpt.values().stream().findFirst().get();
                                var scanSummary = scanOverview.summary();
                                var vulnerabilityCounts = scanSummary.counts();
                                var harborLink = "https://harbor.rsyd.net/harbor/projects/5/repositories/" + gitlabProject.name().toLowerCase().replace(" ", "-");
                                var artifactReport = new ArtifactReport(
                                    harborLink,
                                    latestCommit.shortId(),
                                    scanOverview.severity(),
                                    vulnerabilityCounts.critical(),
                                    vulnerabilityCounts.high(),
                                    vulnerabilityCounts.medium(),
                                    vulnerabilityCounts.low(),
                                    scanSummary.total()
                                );
                                return new ProjectPreview(gitlabProject, artifactReport);
                            })
                            .defaultIfEmpty(new ProjectPreview(gitlabProject, null))
                            .onErrorResume(e -> {

                                /*
                                 * Harbor returns a 404 if no result is found. Micronaut interprets this as a "(404): Client 'harbor': Not Found", which is not the case.
                                 * https://docs.micronaut.io/latest/guide/#clientError
                                 */
                                if (e instanceof HttpClientResponseException httpEx && httpEx.getStatus() == HttpStatus.NOT_FOUND) {
                                    LOG.warn("Artifact not found (404): {}", httpEx.getMessage());
                                } else {
                                    LOG.error("Error fetching artifact: {}", e.getMessage(), e);
                                }
                                return Mono.just(new ProjectPreview(gitlabProject, null));
                            });
                    })
                    .defaultIfEmpty(new ProjectPreview(gitlabProject, null))
            )
            .sort(Comparator.comparing(preview -> preview.gitlabProject().name()));
    }
}