package dk.rsyd.jap.dashboard.project;

import dk.rsyd.jap.dashboard.gitlab.GitlabClient;
import dk.rsyd.jap.dashboard.gitlab.GitlabProject;
import dk.rsyd.jap.dashboard.harbor.ArtifactReportService;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

import java.util.Comparator;

//todo logging
@Singleton
public class ProjectService {

    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final GitlabClient gitlabClient;
    private final ArtifactReportService artifactReportService;

    public ProjectService(GitlabClient gitlabClient, ArtifactReportService artifactReportService) {
        this.gitlabClient = gitlabClient;
        this.artifactReportService = artifactReportService;
    }

    public Flux<ProjectPreview> getProjectPreviews(int perPage, int page) {
    return gitlabClient.fetchProjects(perPage, page)
        .map(GitlabProject::fromJapNameSpace)
        .flatMap(gitlabProject ->
            artifactReportService
                .getArtifactReportFromLatestMasterCommit(gitlabProject.name())
                .map(artifactReport -> new ProjectPreview(gitlabProject, artifactReport))
                .defaultIfEmpty(new ProjectPreview(gitlabProject, null))
        )
        .sort(Comparator.comparing(preview -> preview.gitlabProject().name()));
}
}