package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

//Todo caching
@Client(id = "gitlab")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = "PRIVATE-TOKEN", value = "${gitlab.access-token}")
public interface GitlabClient {

    @Get("/api/v4/groups/6395180/projects?include_subgroups=true&per_page={perPage}&page={page}" +
    //    "&order_by=path&sort=asc" +
        "&order_by=updated_at" +
        "&archived=false")
    Flux<GitlabClientDTOs.GitlabProject> fetchProjects(@QueryValue int perPage, @QueryValue int page);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits?ref_name=master&per_page=5&page=1")
    Flux<GitlabClientDTOs.Commit> fetchCommitsFromMasterBranch(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits?ref_name=main&per_page=5&page=1")
    Flux<GitlabClientDTOs.Commit> fetchCommitsFromMainBranch(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/groups/5026930/projects?include_subgroups=true&search={name}&simple=true")
    Flux<GitlabClientDTOs.GitlabProject> fetchProjectsFromName(@QueryValue String name);

    //TODO perPage might need to be adjusted
    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/jobs?scope=success&per_page=250&page=1")
    Flux<GitlabClientDTOs.Job> fetchSuccessfulJobsFromProject(@QueryValue int projectId);
}
