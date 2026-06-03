package dk.rsyd.jap.dashboard.harbor.artifactReport;

import dk.rsyd.jap.dashboard.gitlab.Commit;
import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import dk.rsyd.jap.dashboard.harbor.client.HarborClient;
import dk.rsyd.jap.dashboard.nomad.NomadService;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

@Singleton
public class ArtifactReportService {

    private static final Logger LOG = LogManager.getLogger(ArtifactReportService.class);
    private final HarborClient harborClient;
    private final GitlabService gitlabService;
    private final NomadService nomadService;

    public ArtifactReportService(HarborClient harborClient, GitlabService gitlabService, NomadService nomadService) {
        this.harborClient = harborClient;
        this.gitlabService = gitlabService;
        this.nomadService = nomadService;
    }

    public Mono<ArtifactReport> getArtifactReportFromLatestMasterCommit(int projectId) {
        return gitlabService.getProject(projectId)
            .flatMap(gitlabProject ->
                gitlabService.fetchLatestCommitFromPrimaryBranch(projectId)
                    .flatMap(latestCommit ->
                        getArtifactReport(gitlabProject.name(), latestCommit)
                            .defaultIfEmpty(
                                ArtifactReport.WithNoArtifactInfo(makeRepositoryURL(gitlabProject.name()), latestCommit)
                            )))
            .doOnError(LOG::error);
    }

    public Mono<ArtifactReport> getArtifactReportFromLatestProdDeploy(int projectId) {
        return gitlabService.getProject(projectId)
            .flatMap(gitlabProject ->
                nomadService.getProdJobFromProjectName(gitlabProject.name())
                    .flatMap(nomadJob ->
                        gitlabService
                            .findCommitFromShortId(projectId, nomadJob.gitCommit())
                            .flatMap(commit -> getArtifactReport(gitlabProject.name(), commit)
                                .defaultIfEmpty(
                                    ArtifactReport.WithNoArtifactInfo(makeRepositoryURL(gitlabProject.name()), commit)
                                ))
                    ))
            .doOnError(LOG::error);
    }

    public Mono<ArtifactReport> getArtifactReport(String projectName, Commit commit) {
        if (commit == null || commit.shortId() == null) {
            return Mono.empty();
        }

        return harborClient.getArtifactFromReference(projectName.toLowerCase(), commit.shortId())
            .flatMap(harborArtifact ->
                harborArtifact.scanOverview()
                    .values().stream().findFirst()
                    .map(scanReport -> Mono.just(
                        ArtifactReport.of(makeRepositoryURL(projectName), commit, makeArtifactURL(projectName, harborArtifact.digest()), scanReport)))
                    .orElseGet(Mono::empty)
            )
            .onErrorResume(e -> handleNotFoundError(projectName, e));
    }

    private Mono<ArtifactReport> handleNotFoundError(String projectName, Throwable e) {
        /*
         * Harbor returns a 404 if no result is found. Micronaut interprets this as a
         * "Client 'harbor': Not Found", which is not the case.
         * https://docs.micronaut.io/latest/guide/#clientError
         */
        if (e.getMessage().contains("Client 'harbor': Not Found")) {
            LOG.debug("ArtifactReport not found (404) for project: {}", projectName);
            return Mono.empty();
        } else {
            LOG.error("Error fetching artifact: {}", e.getMessage(), e);
        }
        return Mono.error(e);
    }

    private static final String HARBOR_REPO_BASE_URL = "https://harbor.rsyd.net/harbor/projects/5/repositories/%s";
    private static final String HARBOR_REPO_ARTIFACT_URL = "https://harbor.rsyd.net/harbor/projects/5/repositories/%s/artifacts-tab/artifacts/%s";

    private static String makeRepositoryURL(String projectName) {
        return String.format(HARBOR_REPO_BASE_URL, projectName.toLowerCase().replace(" ", "-"));
    }

    private static String makeArtifactURL(String projectName, String digest) {
        return String.format(HARBOR_REPO_ARTIFACT_URL, projectName.toLowerCase().replace(" ", "-"), digest);
    }

}
