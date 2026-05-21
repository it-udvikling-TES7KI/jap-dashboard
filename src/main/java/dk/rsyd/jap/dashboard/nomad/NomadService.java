package dk.rsyd.jap.dashboard.nomad;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Singleton
public class NomadService {

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
            .flatMap(nomadJob -> getJob(nomadJob.id()));
    }

    public Mono<NomadJob> getJob(String jobId) {
        return nomadClient.fetchJob(jobId)
            .flatMap(nomadJob -> {
                    var serviceLink = nomadJob
                        .taskGroups()
                        .getFirst()
                        .tasks()
                        .getFirst()
                        .services()
                        .getFirst()
                        .extractTraefikHost();

                    if (serviceLink.isPresent()) {
                        var fullServiceLink = "https://" + serviceLink.get();

                        return checkHealth(fullServiceLink)
                            .map(status -> NomadJob.fromDTO(nomadJob, fullServiceLink, status, LogScaleUrlBuilder.buildLogsUrl(nomadJob.id())));
                    }

                    return Mono.just(NomadJob.fromDTO(nomadJob, null, "UNKNOWN",null));
                }
            );
    }

    private Mono<String> checkHealth(String path) {
        return Mono.fromCallable(() -> URI.create(path))
            .flatMap(healthClient::getHealth)
            .map(HealthClientDTO::status);
    }

}
