package dk.rsyd.jap.dashboard.harbor.artifactReport;

import dk.rsyd.jap.dashboard.gitlab.Commit;
import dk.rsyd.jap.dashboard.gitlab.GitlabProject;
import dk.rsyd.jap.dashboard.gitlab.GitlabService;
import dk.rsyd.jap.dashboard.harbor.client.HarborClient;
import dk.rsyd.jap.dashboard.harbor.client.HarborClientDTOs;
import dk.rsyd.jap.dashboard.nomad.NomadJob;
import dk.rsyd.jap.dashboard.nomad.NomadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactReportServiceTest {

    @Mock
    private HarborClient harborClient;

    @Mock
    private GitlabService gitlabService;

    @Mock
    private NomadService nomadService;

    private ArtifactReportService artifactReportService;

    @BeforeEach
    void setUp() {
        artifactReportService = new ArtifactReportService(harborClient, gitlabService, nomadService);
    }

    private GitlabProject gitlabProject(int id, String name) {
        return new GitlabProject(id, name, "team/subteam", "https://gitlab.rsyd.net/team/subteam/" + name);
    }

    private Commit commit(String shortId) {
        return new Commit(
            "full-sha-" + shortId,
            shortId,
            "commit title",
            "https://gitlab.rsyd.net/team/proj/-/commit/" + shortId,
            "author@rsyd.dk",
            LocalDateTime.of(2026, 1, 1, 0, 0)
        );
    }

    private NomadJob nomadJobWithGitCommit(String projectName, String gitCommit) {
        return new NomadJob(
            projectName + "-prod",
            projectName + "-prod",
            gitCommit,
            "https://" + projectName + ".rsyd.net",
            "running",
            "UP",
            "https://" + projectName + ".rsyd.net/health",
            "https://nomad.rsyd.net/ui/jobs/" + projectName + "-prod@jap",
            "https://logscale.example",
            "https://" + projectName + ".rsyd.net/docs"
        );
    }

    private HarborClientDTOs.ScanReport scanReport() {
        HarborClientDTOs.VulnerabilityCounts counts = new HarborClientDTOs.VulnerabilityCounts(1, 2, 3, 6);
        HarborClientDTOs.ScanSummary summary = new HarborClientDTOs.ScanSummary(counts, 3, 12);
        return new HarborClientDTOs.ScanReport(summary, "HIGH");
    }

    private HarborClientDTOs.Artifact artifact(String digest, HarborClientDTOs.ScanReport scanReport) {
        return new HarborClientDTOs.Artifact(
            "2026-01-01T00:00:00Z",
            List.of(),
            digest,
            Map.of("application/vnd.security.vulnerability.report; version=1.1", scanReport)
        );
    }

    @Test
    void getArtifactReport_returnsEmptyWhenCommitIsNull() {
        ArtifactReport result = artifactReportService.getArtifactReport("my-service", null).block();
        assertNull(result);
    }

    @Test
    void getArtifactReport_returnsEmptyWhenCommitShortIdIsNull() {
        Commit commit = new Commit(
            "full-sha",
            null,
            "title",
            "https://gitlab.rsyd.net/team/proj/-/commit/full-sha",
            "author@rsyd.dk",
            LocalDateTime.of(2026, 1, 1, 0, 0)
        );
        ArtifactReport result = artifactReportService.getArtifactReport("my-service", commit).block();
        assertNull(result);
    }

    @Test
    void getArtifactReport_returnsReportWhenArtifactAndScanOverviewExist() {
        Commit commit = commit("abc1234");
        HarborClientDTOs.Artifact artifact = artifact("sha256:deadbeef", scanReport());

        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.just(artifact));

        ArtifactReport result = artifactReportService.getArtifactReport("my-service", commit).block();

        assertNotNull(result);
        assertEquals("https://harbor.rsyd.net/harbor/projects/5/repositories/my-service", result.repositoryURL());
        assertEquals("abc1234", result.commitShortId());
        assertEquals("https://gitlab.rsyd.net/team/proj/-/commit/abc1234", result.commitURL());
        assertEquals("https://harbor.rsyd.net/harbor/projects/5/repositories/my-service/artifacts-tab/artifacts/sha256:deadbeef", result.artifactURL());
        assertEquals("HIGH", result.severity());
        assertEquals(1, result.critical());
        assertEquals(2, result.high());
        assertEquals(3, result.medium());
        assertEquals(6, result.low());
        assertEquals(12, result.total());
        assertEquals(3, result.fixable());
    }

    @Test
    void getArtifactReport_returnsEmptyWhenScanOverviewIsEmpty() {
        Commit commit = commit("abc1234");
        HarborClientDTOs.Artifact artifact = new HarborClientDTOs.Artifact(
            "2026-01-01T00:00:00Z",
            List.of(),
            "sha256:deadbeef",
            Map.of()
        );

        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.just(artifact));

        ArtifactReport result = artifactReportService.getArtifactReport("my-service", commit).block();

        assertNull(result);
    }

    @Test
    void getArtifactReport_returnsEmptyOnHarborNotFoundMessage() {
        Commit commit = commit("abc1234");

        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.error(new RuntimeException("Client 'harbor': Not Found")));

        ArtifactReport result = artifactReportService.getArtifactReport("my-service", commit).block();

        assertNull(result);
    }

    @Test
    void getArtifactReport_propagatesOtherErrors() {
        Commit commit = commit("abc1234");

        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.error(new RuntimeException("boom")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            artifactReportService.getArtifactReport("my-service", commit).block()
        );
        assertTrue(ex.getMessage().contains("boom"));
    }

    @Test
    void getArtifactReportFromLatestMasterCommit_returnsReportWhenArtifactFound() {
        int projectId = 1;
        String projectName = "my-service";
        Commit latest = commit("abc1234");
        HarborClientDTOs.Artifact artifact = artifact("sha256:deadbeef", scanReport());

        when(gitlabService.getProject(projectId))
            .thenReturn(Mono.just(gitlabProject(projectId, projectName)));
        when(gitlabService.fetchLatestCommitFromPrimaryBranch(projectId))
            .thenReturn(Mono.just(latest));
        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.just(artifact));

        ArtifactReport result = artifactReportService.getArtifactReportFromLatestMasterCommit(projectId).block();

        assertNotNull(result);
        assertEquals("abc1234", result.commitShortId());
        assertNotNull(result.artifactURL());
    }

    @Test
    void getArtifactReportFromLatestMasterCommit_returnsWithNoArtifactInfoWhenNoArtifact() {
        int projectId = 1;
        String projectName = "my-service";
        Commit latest = commit("abc1234");

        when(gitlabService.getProject(projectId))
            .thenReturn(Mono.just(gitlabProject(projectId, projectName)));
        when(gitlabService.fetchLatestCommitFromPrimaryBranch(projectId))
            .thenReturn(Mono.just(latest));
        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.empty());

        ArtifactReport result = artifactReportService.getArtifactReportFromLatestMasterCommit(projectId).block();

        assertNotNull(result);
        assertEquals("https://harbor.rsyd.net/harbor/projects/5/repositories/my-service", result.repositoryURL());
        assertEquals("abc1234", result.commitShortId());
        assertEquals("https://gitlab.rsyd.net/team/proj/-/commit/abc1234", result.commitURL());
        assertNull(result.artifactURL());
        assertNull(result.severity());
        assertEquals(0, result.critical());
        assertEquals(0, result.high());
        assertEquals(0, result.medium());
        assertEquals(0, result.low());
        assertEquals(0, result.total());
        assertEquals(0, result.fixable());
    }

    @Test
    void getArtifactReportFromLatestProdDeploy_returnsReportWhenArtifactFound() {
        int projectId = 1;
        String projectName = "my-service";
        String shortId = "abc1234";
        Commit commit = commit(shortId);
        HarborClientDTOs.Artifact artifact = artifact("sha256:deadbeef", scanReport());

        when(gitlabService.getProject(projectId))
            .thenReturn(Mono.just(gitlabProject(projectId, projectName)));
        when(nomadService.getProdJobFromProjectName(projectName))
            .thenReturn(Mono.just(nomadJobWithGitCommit(projectName, shortId)));
        when(gitlabService.findCommitFromShortId(projectId, shortId))
            .thenReturn(Mono.just(commit));
        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.just(artifact));

        ArtifactReport result = artifactReportService.getArtifactReportFromLatestProdDeploy(projectId).block();

        assertNotNull(result);
        assertEquals("abc1234", result.commitShortId());
        assertNotNull(result.artifactURL());
    }

    @Test
    void getArtifactReportFromLatestProdDeploy_returnsWithNoArtifactInfoWhenNoArtifact() {
        int projectId = 1;
        String projectName = "my-service";
        String shortId = "abc1234";
        Commit commit = commit(shortId);

        when(gitlabService.getProject(projectId))
            .thenReturn(Mono.just(gitlabProject(projectId, projectName)));
        when(nomadService.getProdJobFromProjectName(projectName))
            .thenReturn(Mono.just(nomadJobWithGitCommit(projectName, shortId)));
        when(gitlabService.findCommitFromShortId(projectId, shortId))
            .thenReturn(Mono.just(commit));
        when(harborClient.getArtifactFromReference("my-service", "abc1234"))
            .thenReturn(Mono.empty());

        ArtifactReport result = artifactReportService.getArtifactReportFromLatestProdDeploy(projectId).block();

        assertNotNull(result);
        assertEquals("https://harbor.rsyd.net/harbor/projects/5/repositories/my-service", result.repositoryURL());
        assertEquals("abc1234", result.commitShortId());
        assertEquals("https://gitlab.rsyd.net/team/proj/-/commit/abc1234", result.commitURL());
        assertNull(result.artifactURL());
        assertNull(result.severity());
        assertEquals(0, result.critical());
        assertEquals(0, result.high());
        assertEquals(0, result.medium());
        assertEquals(0, result.low());
        assertEquals(0, result.total());
        assertEquals(0, result.fixable());
    }
}