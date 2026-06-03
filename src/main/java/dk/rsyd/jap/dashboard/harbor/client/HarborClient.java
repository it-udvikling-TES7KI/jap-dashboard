package dk.rsyd.jap.dashboard.harbor.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import static io.micronaut.http.HttpHeaders.USER_AGENT;

//Todo caching
@BasicAuth
@Client(id = "harbor")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
public interface HarborClient {

    @Get(
        "projects/${harbor.project}/repositories/{projectName}/artifacts/{reference}" +
            "?page=1&page_size=1" +
            "&with_tag=true&with_label=true" +
            "&with_scan_overview=true" +
            "&with_sbom_overview=true" +
            "&with_accessory=false" +
            "&with_signature=false" +
            "&with_immutable_status=false"
    )
    Mono<HarborClientDTOs.Artifact> getArtifactFromReference(@QueryValue String projectName, @QueryValue String reference);
}
