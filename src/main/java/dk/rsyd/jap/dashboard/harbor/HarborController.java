package dk.rsyd.jap.dashboard.harbor;

import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReport;
import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReportService;
import dk.rsyd.jap.dashboard.harbor.client.HarborClient;
import dk.rsyd.jap.dashboard.harbor.client.HarborClientDTOs;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/harbor")
public class HarborController {

    private static final Logger LOG = LogManager.getLogger(HarborController.class);

    private final HarborClient harborClient;
    private final ArtifactReportService artifactReportService;


    public HarborController(HarborClient harborClient, ArtifactReportService artifactReportService) {
        this.harborClient = harborClient;
        this.artifactReportService = artifactReportService;
    }


    @Get("/project/{projectId}/artifactReport/latestMasterCommit")
    Mono<ArtifactReport> getArtifactReportFromLatestMasterCommit(HttpRequest<?> httpRequest, @PathVariable int projectId){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return artifactReportService.getArtifactReportFromLatestMasterCommit(projectId);
    }

    @Get("/project/{projectId}/artifactReport/latestProdDeploy")
    Mono<ArtifactReport> getArtifactReportFromLatestProdDeploy(HttpRequest<?> httpRequest, @PathVariable int projectId) {
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return artifactReportService.getArtifactReportFromLatestProdDeploy(projectId);
    }

    //todo not in use
    @Get("/projects")
    Flux<HarborClientDTOs.Artifact> getHarborProjectFromProjectName(HttpRequest<?> httpRequest, @QueryValue String projectName){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return harborClient.getArtifactsFromProjectName(projectName);
    }

    //todo not in use
    @Get("/artifacts")
    Mono<HarborClientDTOs.Artifact> getHarborArtifactFromReference(HttpRequest<?> httpRequest, @QueryValue String projectName, @QueryValue String reference){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return harborClient.getArtifactFromReference(projectName, reference);
    }
}
