package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

//Todo caching
@Client(id = "gitlab")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = "PRIVATE-TOKEN", value = "${gitlab.access-token}")
public interface GitlabClient {

    @Get(value = "/api/v4/groups/6395180/projects?" +
        "include_subgroups=true&" +
        "per_page={perPage}&page={page}" +
        "&order_by=path&sort=asc" +
        "&archived=false")
    Flux<GitlabClientDTOs.GitlabProject> fetchProjects(@PathVariable int perPage, @PathVariable int page);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}")
    Mono<GitlabClientDTOs.GitlabProject> fetchProjectFromId(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits?ref_name=master&per_page=5&page=1")
    Flux<GitlabClientDTOs.Commit> fetchLatestCommitsFromMasterBranch(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits?ref_name=main&per_page=5&page=1")
    Flux<GitlabClientDTOs.Commit> fetchLatestCommitsFromMainBranch(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits/{sha}")
    Mono<GitlabClientDTOs.Commit> fetchCommitFromSHA(@QueryValue int projectId, @QueryValue String sha);
}
