package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import java.net.URI;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client("/")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
public interface HealthClient {

    @Header(name = ACCEPT, value = MediaType.APPLICATION_JSON)
    @Get("{+url}/health")
    Mono<HealthClientDTO> getHealth(@QueryValue("url") URI url);
}