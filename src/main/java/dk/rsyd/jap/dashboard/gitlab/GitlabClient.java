package dk.rsyd.jap.dashboard.gitlab;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(id = "gitlab")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/json")
@Header(name = "PRIVATE-TOKEN", value = "${gitlab.access-token}")
public interface GitlabClient {

    @Get("/api/v4/groups/5026930/projects?include_subgroups=true&per_page=20&page={page}")
    Mono<HttpResponse<List<GitlabProject>>> fetchProjectsWithHeaders(@QueryValue int page);

}
