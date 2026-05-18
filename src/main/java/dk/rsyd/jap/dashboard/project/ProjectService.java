package dk.rsyd.jap.dashboard.project;

import dk.rsyd.jap.dashboard.gitlab.GitlabClient;
import dk.rsyd.jap.dashboard.gitlab.GitlabProject;
import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import dk.rsyd.jap.dashboard.harbor.ArtifactReportService;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//todo logging
@Singleton
public class ProjectService {

    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final GitlabClient gitlabClient;
    private final GitlabService gitlabService;
    private final ArtifactReportService artifactReportService;

    public ProjectService(GitlabClient gitlabClient, GitlabService gitlabService, ArtifactReportService artifactReportService) {
        this.gitlabClient = gitlabClient;
        this.gitlabService = gitlabService;
        this.artifactReportService = artifactReportService;
    }

    public Flux<ProjectPreview> getProjectPreviews(int perPage, int page) {
        return gitlabClient.fetchProjects(perPage, page)
            .map(GitlabProject::fromJapNameSpace)
            .flatMapSequential(gitlabProject -> {
                var latestProdDeploy = artifactReportService
                    .getArtifactReportFromLatestProdDeploy(gitlabProject.name())
                    .singleOptional();

                var latestMasterCommit = artifactReportService
                    .getArtifactReportFromLatestMasterCommit(gitlabProject.name())
                    .singleOptional();

                return Mono.zip(
                    latestProdDeploy,
                    latestMasterCommit,
                    (prodDeploy, masterCommit) -> new ProjectPreview(gitlabProject, masterCommit.orElse(null), prodDeploy.orElse(null))
                );
            });
    }
}