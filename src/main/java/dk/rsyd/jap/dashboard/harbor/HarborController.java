package dk.rsyd.jap.dashboard.harbor;

import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReport;
import dk.rsyd.jap.dashboard.harbor.artifactReport.ArtifactReportService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/harbor")
public class HarborController {

    private static final Logger LOG = LogManager.getLogger(HarborController.class);

    private final ArtifactReportService artifactReportService;


    public HarborController(ArtifactReportService artifactReportService) {
        this.artifactReportService = artifactReportService;
    }


    @Get("/project/{projectId}/artifactReport/latestMasterCommit")
    Mono<MutableHttpResponse<ArtifactReport>> getArtifactReportFromLatestMasterCommit(HttpRequest<?> httpRequest, @PathVariable int projectId){
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return artifactReportService.getArtifactReportFromLatestMasterCommit(projectId)
            .map(HttpResponse::ok)
            .defaultIfEmpty(HttpResponse.noContent());
    }

    @Get("/project/{projectId}/artifactReport/latestProdDeploy")
    Mono<MutableHttpResponse<ArtifactReport>> getArtifactReportFromLatestProdDeploy(HttpRequest<?> httpRequest, @PathVariable int projectId) {
        LOG.info("method={}, endpoint={}", httpRequest.getMethod(), httpRequest.getUri());

        return artifactReportService.getArtifactReportFromLatestProdDeploy(projectId)
            .map(HttpResponse::ok)
            .defaultIfEmpty(HttpResponse.noContent());
    }
}
