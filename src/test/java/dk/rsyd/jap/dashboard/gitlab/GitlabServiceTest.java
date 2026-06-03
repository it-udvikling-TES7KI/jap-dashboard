package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitlabServiceTest {

    @Mock
    private GitlabClient gitlabClient;

    private GitlabService gitlabService;

    @BeforeEach
    void setUp() {
        gitlabService = new GitlabService(gitlabClient);
    }

    // helpers

    private GitlabClientDTOs.GitlabProject gitlabProjectDTO(int id, String name) {
        return new GitlabClientDTOs.GitlabProject(
            id,
            name,
            "jap / " + name,
            "https://gitlab.com/group/jap/" + name
        );
    }

    private GitlabClientDTOs.Commit commitDTO(String id, String shortId, String title) {
        return new GitlabClientDTOs.Commit(
            id,
            shortId,
            OffsetDateTime.of(2026, 6, 4, 10, 0, 0, 0, ZoneOffset.UTC),
            "https://gitlab.com/group/jap/project/-/commit/" + id,
            title,
            "author@example.com"
        );
    }

    // getProjects

    @Test
    void getProjects_returnsMappedProjects() {
        int perPage = 20;
        int page = 1;
        GitlabClientDTOs.GitlabProject dto1 = gitlabProjectDTO(1, "project-1");
        GitlabClientDTOs.GitlabProject dto2 = gitlabProjectDTO(2, "project-2");

        when(gitlabClient.fetchProjects(perPage, page))
            .thenReturn(Flux.just(dto1, dto2));

        List<GitlabProject> result = gitlabService.getProjects(perPage, page).collectList().block();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals("project-1", result.get(0).name());
        assertEquals(2, result.get(1).id());
        assertEquals("project-2", result.get(1).name());
    }

    @Test
    void getProjects_returnsEmptyFluxWhenNoProjects() {
        when(gitlabClient.fetchProjects(anyInt(), anyInt()))
            .thenReturn(Flux.empty());

        List<GitlabProject> result = gitlabService.getProjects(20, 1).collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProjects_propagatesErrors() {
        when(gitlabClient.fetchProjects(anyInt(), anyInt()))
            .thenReturn(Flux.error(new RuntimeException("API error")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            gitlabService.getProjects(20, 1).collectList().block()
        );
        assertTrue(ex.getMessage().contains("API error"));
    }

    // fetchLatestCommitFromPrimaryBranch

    @Test
    void fetchLatestCommitFromPrimaryBranch_returnsCommitFromMasterBranch() {
        int projectId = 123;
        GitlabClientDTOs.Commit commitDto = commitDTO("abc123", "abc12", "Initial commit");

        when(gitlabClient.fetchLatestCommitsFromMasterBranch(projectId))
            .thenReturn(Flux.just(commitDto));
        when(gitlabClient.fetchLatestCommitsFromMainBranch(projectId))
            .thenReturn(Flux.just(commitDto));

        Commit result = gitlabService.fetchLatestCommitFromPrimaryBranch(projectId).block();

        assertNotNull(result);
        assertEquals("abc123", result.id());
        assertEquals("abc12", result.shortId());
        assertEquals("Initial commit", result.title());
    }

    @Test
    void fetchLatestCommitFromPrimaryBranch_fallsBackToMainWhenMasterEmpty() {
        int projectId = 123;
        GitlabClientDTOs.Commit commitDto = commitDTO("def456", "def45", "Fallback commit");

        when(gitlabClient.fetchLatestCommitsFromMasterBranch(projectId))
            .thenReturn(Flux.empty());
        when(gitlabClient.fetchLatestCommitsFromMainBranch(projectId))
            .thenReturn(Flux.just(commitDto));

        Commit result = gitlabService.fetchLatestCommitFromPrimaryBranch(projectId).block();

        assertNotNull(result);
        assertEquals("def456", result.id());
        assertEquals("def45", result.shortId());
    }

    @Test
    void fetchLatestCommitFromPrimaryBranch_returnsFirstCommitWhenMultiple() {
        int projectId = 123;
        GitlabClientDTOs.Commit commit1 = commitDTO("abc123", "abc12", "First commit");
        GitlabClientDTOs.Commit commit2 = commitDTO("def456", "def45", "Second commit");

        when(gitlabClient.fetchLatestCommitsFromMasterBranch(projectId))
            .thenReturn(Flux.just(commit1, commit2));

        when(gitlabClient.fetchLatestCommitsFromMainBranch(projectId))
            .thenReturn(Flux.empty());

        Commit result = gitlabService.fetchLatestCommitFromPrimaryBranch(projectId).block();

        assertNotNull(result);
        assertEquals("abc123", result.id());
    }

    @Test
    void fetchLatestCommitFromPrimaryBranch_returnsEmptyWhenNoCommitsFound() {
        int projectId = 123;

        when(gitlabClient.fetchLatestCommitsFromMasterBranch(projectId))
            .thenReturn(Flux.empty());
        when(gitlabClient.fetchLatestCommitsFromMainBranch(projectId))
            .thenReturn(Flux.empty());

        Commit result = gitlabService.fetchLatestCommitFromPrimaryBranch(projectId).block();

        assertNull(result);
    }

    @Test
    void fetchLatestCommitFromPrimaryBranch_propagatesErrors() {
        int projectId = 123;

        when(gitlabClient.fetchLatestCommitsFromMasterBranch(projectId))
            .thenReturn(Flux.error(new Exception("master branch error")));
        when(gitlabClient.fetchLatestCommitsFromMainBranch(projectId))
            .thenReturn(Flux.error(new Exception("main branch error")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            gitlabService.fetchLatestCommitFromPrimaryBranch(projectId).block()
        );
        assertTrue(ex.getMessage().contains("branch error"));
    }

    // getProject

    @Test
    void getProject_returnsMappedProject() {
        int projectId = 123;
        GitlabClientDTOs.GitlabProject dto = gitlabProjectDTO(projectId, "my-project");

        when(gitlabClient.fetchProjectFromId(projectId))
            .thenReturn(Mono.just(dto));

        GitlabProject result = gitlabService.getProject(projectId).block();

        assertNotNull(result);
        assertEquals(projectId, result.id());
        assertEquals("my-project", result.name());
    }

    @Test
    void getProject_propagatesErrorsFrom404() {
        int projectId = 999;

        when(gitlabClient.fetchProjectFromId(projectId))
            .thenReturn(Mono.error(new HttpClientResponseException("Not Found", HttpResponse.status(HttpStatus.NOT_FOUND))));

        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class, () ->
            gitlabService.getProject(projectId).block()
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getProject_propagatesOtherErrors() {
        int projectId = 123;

        when(gitlabClient.fetchProjectFromId(projectId))
            .thenReturn(Mono.error(new RuntimeException("API error")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            gitlabService.getProject(projectId).block()
        );
        assertTrue(ex.getMessage().contains("API error"));
    }

    // findCommitFromShortId

    @Test
    void findCommitFromShortId_returnsMappedCommit() {
        int projectId = 123;
        String shortId = "abc12345";
        GitlabClientDTOs.Commit dto = commitDTO("abc123456789", shortId, "Fix bug");

        when(gitlabClient.fetchCommitFromSHA(projectId, shortId))
            .thenReturn(Mono.just(dto));

        Commit result = gitlabService.findCommitFromShortId(projectId, shortId).block();

        assertNotNull(result);
        assertEquals("abc123456789", result.id());
        assertEquals(shortId, result.shortId());
        assertEquals("Fix bug", result.title());
    }

    @Test
    void findCommitFromShortId_propagatesErrors() {
        int projectId = 123;
        String shortId = "invalid";

        when(gitlabClient.fetchCommitFromSHA(projectId, shortId))
            .thenReturn(Mono.error(new RuntimeException("Commit not found")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            gitlabService.findCommitFromShortId(projectId, shortId).block()
        );
        assertTrue(ex.getMessage().contains("Commit not found"));
    }

    @Test
    void findCommitFromShortId_mapsCommitDTOCorrectly() {
        int projectId = 123;
        String shortId = "xyz789";
        String commitTitle = "Refactor code";
        String authorEmail = "dev@example.com";
        GitlabClientDTOs.Commit dto = new GitlabClientDTOs.Commit(
            "xyz789abcdef",
            shortId,
            OffsetDateTime.of(2026, 6, 3, 15, 30, 0, 0, ZoneOffset.UTC),
            "https://gitlab.com/commit/url",
            commitTitle,
            authorEmail
        );

        when(gitlabClient.fetchCommitFromSHA(projectId, shortId))
            .thenReturn(Mono.just(dto));

        Commit result = gitlabService.findCommitFromShortId(projectId, shortId).block();

        assertNotNull(result);
        assertEquals("xyz789abcdef", result.id());
        assertEquals(shortId, result.shortId());
        assertEquals(commitTitle, result.title());
        assertEquals(authorEmail, result.authorEmail());
        assertEquals("https://gitlab.com/commit/url", result.gitlabURL());
    }
}