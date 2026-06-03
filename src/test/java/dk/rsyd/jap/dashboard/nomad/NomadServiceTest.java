package dk.rsyd.jap.dashboard.nomad;

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

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NomadServiceTest {

    @Mock
    private NomadClient nomadClient;

    @Mock
    private HealthClient healthClient;

    private NomadService nomadService;

    @BeforeEach
    void setUp() {
        nomadService = new NomadService(nomadClient, healthClient);
    }

    // helpers

    private NomadClientDTOs.NomadJobSimple simpleJob(String id) {
        return new NomadClientDTOs.NomadJobSimple(id, id, null, "running");
    }

    private NomadClientDTOs.NomadJob fullJob(String id, String traefikTag) {
        var service = new NomadClientDTOs.Service(
            traefikTag != null ? List.of(traefikTag) : List.of()
        );
        var config = new NomadClientDTOs.Config("harbor.rsyd.net/jap/" + id + ":abc1234");
        var task = new NomadClientDTOs.Task(List.of(service), config);
        var group = new NomadClientDTOs.TaskGroup("web", List.of(task));
        return new NomadClientDTOs.NomadJob(id, id, null, "running", List.of(group));
    }

    // getFromProjectName

    @Test
    void getFromProjectName_returnsJobsWithHealthStatus() {
        String jobId = "my-service-prod";
        String traefikTag = "traefik.http.routers.my-service.rule=Host(`my-service.rsyd.net`)";

        when(nomadClient.fetchJobsWithFilter(anyString()))
            .thenReturn(Flux.just(simpleJob(jobId)));
        when(nomadClient.fetchJob(jobId))
            .thenReturn(Mono.just(fullJob(jobId, traefikTag)));
        when(healthClient.getHealth(any(URI.class)))
            .thenReturn(Mono.just(new HealthClientDTO("UP")));

        List<NomadJob> result = nomadService.getFromProjectName("my-service").collectList().block();

        assertNotNull(result);
        assertEquals(1, result.size());
        NomadJob job = result.get(0);
        assertEquals(jobId, job.id());
        assertEquals("UP", job.healthStatus());
        assertEquals("https://my-service.rsyd.net", job.serviceURL());
    }

    @Test
    void getFromProjectName_skipsJobWhenFetchJobReturns404() {
        when(nomadClient.fetchJobsWithFilter(anyString()))
            .thenReturn(Flux.just(simpleJob("missing-service-prod")));
        when(nomadClient.fetchJob("missing-service-prod"))
            .thenReturn(Mono.error(new HttpClientResponseException("Not Found", HttpResponse.status(HttpStatus.NOT_FOUND))));

        List<NomadJob> result = nomadService.getFromProjectName("missing-service").collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFromProjectName_propagatesNonHttpErrors() {
        when(nomadClient.fetchJobsWithFilter(anyString()))
            .thenReturn(Flux.just(simpleJob("bad-service")));
        when(nomadClient.fetchJob("bad-service"))
            .thenReturn(Mono.error(new RuntimeException("something went wrong")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            nomadService.getFromProjectName("bad").collectList().block()
        );
        assertTrue(ex.getMessage().contains("something went wrong"));
    }

    // getProdJobFromProjectName

    @Test
    void getProdJobFromProjectName_returnsFirstProdJob() {
        String jobId = "my-service-prod";
        String traefikTag = "traefik.http.routers.my-service.rule=Host(`my-service.rsyd.net`)";

        when(nomadClient.fetchJobsWithFilter(anyString()))
            .thenReturn(Flux.just(simpleJob(jobId)));
        when(nomadClient.fetchJob(jobId))
            .thenReturn(Mono.just(fullJob(jobId, traefikTag)));
        when(healthClient.getHealth(any(URI.class)))
            .thenReturn(Mono.just(new HealthClientDTO("UP")));

        NomadJob job = nomadService.getProdJobFromProjectName("my-service").block();

        assertNotNull(job);
        assertEquals(jobId, job.id());
    }

    @Test
    void getProdJobFromProjectName_returnsNullWhenNoProdJobFound() {
        when(nomadClient.fetchJobsWithFilter(anyString()))
            .thenReturn(Flux.empty());

        NomadJob job = nomadService.getProdJobFromProjectName("no-service").block();

        assertNull(job);
    }

    // getJob

    @Test
    void getJob_setsUnknownHealthWhenNoTraefikTag() {
        String jobId = "my-service-prod";

        when(nomadClient.fetchJob(jobId))
            .thenReturn(Mono.just(fullJob(jobId, null)));

        NomadJob job = nomadService.getJob(jobId).block();

        assertNotNull(job);
        assertEquals("UNKNOWN", job.healthStatus());
        assertNull(job.serviceURL());
    }

    @Test
    void getJob_setsHealthStatusFromHealthClient() {
        String jobId = "my-service-prod";
        String traefikTag = "traefik.http.routers.my-service.rule=Host(`my-service.rsyd.net`)";

        when(nomadClient.fetchJob(jobId))
            .thenReturn(Mono.just(fullJob(jobId, traefikTag)));
        when(healthClient.getHealth(any(URI.class)))
            .thenReturn(Mono.just(new HealthClientDTO("DOWN")));

        NomadJob job = nomadService.getJob(jobId).block();

        assertNotNull(job);
        assertEquals("DOWN", job.healthStatus());
        assertEquals("https://my-service.rsyd.net", job.serviceURL());
        assertEquals("https://my-service.rsyd.net/health", job.healthURL());
        assertNotNull(job.logscaleURL());
    }

    @Test
    void getJob_returnsNullOn404() {
        when(nomadClient.fetchJob("gone"))
            .thenReturn(Mono.error(new HttpClientResponseException("Not Found", HttpResponse.status(HttpStatus.NOT_FOUND))));

        NomadJob job = nomadService.getJob("gone").block();

        assertNull(job);
    }

    @Test
    void getJob_swallowsUnexpectedErrorMessage() {
        when(nomadClient.fetchJob("bad"))
            .thenReturn(Mono.error(new RuntimeException("Unexpected error occurred")));

        NomadJob job = nomadService.getJob("bad").block();

        assertNull(job);
    }

    @Test
    void getJob_propagatesOtherErrors() {
        when(nomadClient.fetchJob("bad"))
            .thenReturn(Mono.error(new RuntimeException("failure")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            nomadService.getJob("bad").block()
        );
        assertTrue(ex.getMessage().contains("failure"));
    }
}