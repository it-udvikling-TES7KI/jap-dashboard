package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(id = "gitlab")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = "PRIVATE-TOKEN", value = "${gitlab.access-token}")
public interface GitlabClient {

    @Get("/api/v4/groups/5026930/projects?include_subgroups=true&per_page={perPage}&page={page}")
    Flux<GitlabProject> fetchProjects(@QueryValue int perPage, @QueryValue int page);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/projects/{projectId}/repository/commits?ref_name=master&per_page=5&page=1")
    Flux<Commit> fetchCommitsFromMasterBranch(@QueryValue int projectId);

    @Header(name = ACCEPT, value = "application/json")
    @Get("/api/v4/groups/5026930/projects?include_subgroups=true&search={name}&simple=true")
    Flux<GitlabProject> fetchProjectsFromName(@QueryValue String name);

}
