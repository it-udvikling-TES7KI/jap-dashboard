package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

//Todo caching
@Client(id = "nomad")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = "X-Nomad-Token", value = "${nomad.secret-id}")
public interface NomadClient {

    @Header(name = ACCEPT, value = MediaType.APPLICATION_JSON_STREAM)
    @Get("/v1/jobs?namespace=jap")
    Flux<NomadClientDTOs.NomadJobSimple> fetchJapJobs();

    @Header(name = ACCEPT, value = MediaType.APPLICATION_JSON_STREAM)
    @Get("/v1/jobs?namespace=jap&filter={filter}")
    Flux<NomadClientDTOs.NomadJobSimple> fetchJobsWithFilter(@QueryValue("filter") String filter);

    @Header(name = ACCEPT, value = MediaType.APPLICATION_JSON_STREAM)
    @Get("/v1/job/{jobId}?namespace=jap")
    Mono<NomadClientDTOs.NomadJob> fetchJob(@QueryValue("jobId") String jobId);

}
