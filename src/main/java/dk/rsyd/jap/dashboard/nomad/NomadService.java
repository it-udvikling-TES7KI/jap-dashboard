package dk.rsyd.jap.dashboard.nomad;

import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@Singleton
public class NomadService {

    private static final Logger LOG = LogManager.getLogger(NomadService.class);

    private final NomadClient nomadClient;
    private final HealthClient healthClient;


    public NomadService(NomadClient nomadClient, HealthClient healthClient) {
        this.nomadClient = nomadClient;
        this.healthClient = healthClient;
    }

    public Flux<NomadJob> getFromProjectName(String projectName) {

        String filter = "\"" + projectName.toLowerCase() + "\" in Name";

        return nomadClient
            .fetchJobsWithFilter(filter)
            .flatMap(nomadJob -> getJob(nomadJob.id())
                .onErrorResume(e -> handleNotFoundError(projectName, e)));
    }

    public Mono<NomadJob> getProdJobFromProjectName(String projectName) {
        String filter = "\"" + projectName.toLowerCase() + "\" in Name and \"-prod\"  in Name";

        return nomadClient
            .fetchJobsWithFilter(filter)
            .flatMap(nomadJob -> getJob(nomadJob.id()))
            .next()
            .onErrorResume(e -> handleNotFoundError(projectName, e));
    }

    public Mono<NomadJob> getJob(String jobId) {
        return nomadClient.fetchJob(jobId)
            .flatMap(nomadJob -> {
                Optional<String> serviceLink = nomadJob
                        .taskGroups()
                        .getFirst()
                        .tasks()
                        .getFirst()
                        .services()
                        .getFirst()
                        .extractTraefikHost();

                    if (serviceLink.isPresent()) {
                        String fullServiceLink = "https://" + serviceLink.get();

                        return checkHealth(fullServiceLink)
                            .map(status -> NomadJob.fromDTO(nomadJob, fullServiceLink, status, LogScaleUrlBuilder.buildLogsUrl(nomadJob.id())));
                    }

                    return Mono.just(NomadJob.fromDTO(nomadJob, null, "UNKNOWN", null));
                }
            )
            .onErrorResume(e -> handleNotFoundError(jobId, e));
    }

    private Mono<String> checkHealth(String path) {
        return Mono.fromCallable(() -> URI.create(path))
            .flatMap(healthClient::getHealth)
            .map(HealthClientDTO::status);
    }

    private Mono<NomadJob> handleNotFoundError(String identifier, Throwable e) {
        /*
         * Nomad returns a 404 HTML page if no result is found, which cant be interpreted correctly.
         * https://docs.micronaut.io/latest/guide/#clientError
         */
        if (e instanceof HttpClientResponseException || e.getMessage().contains("Unexpected error")) {
            LOG.debug("not found (404) for NomadProject: {}", identifier);
            return Mono.empty();
        }

        LOG.error("Error nomadJob: {}", e.getMessage(), e);

        return Mono.error(e);
    }

}
