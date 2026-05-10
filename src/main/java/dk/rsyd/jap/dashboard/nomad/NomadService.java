package dk.rsyd.jap.dashboard.nomad;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Singleton
public class NomadService {

    private final NomadClient nomadClient;


    public NomadService(NomadClient nomadClient) {
        this.nomadClient = nomadClient;
    }

    public Flux<NomadJob> getAll() {
        return nomadClient.fetchJapJobs().doOnNext(nomadJob -> {
            nomadJob.setServiceLink(findWorkingLink(nomadJob));
        });
    }

    public Flux<NomadJob> getFromProjectName(String projectName) {

        String filter = "\"" + projectName.toLowerCase() + "\" in Name";

        return nomadClient.fetchJobsWithFilter(filter).doOnNext(nomadJob -> nomadJob.setServiceLink(findWorkingLink(nomadJob)));

    }


    /**
     * tries different known patterns to find the path leading to the service
     *
     * @return a path, if such is found, else null.
     */
    private String findWorkingLink(NomadJob nomadJob) {

        //Todo make this more understandable
        String nameFirst = "https://" + nomadJob.findEnv() + "-" +
            nomadJob.getName().replace("-" + nomadJob.findEnv(), "") + ".jap.rsyd";
        String envFirst = "https://" + nomadJob.getName() + ".jap.rsyd";

        String[] patterns = {nameFirst, envFirst};
        for (String pattern : patterns) {
            String path = pattern + ".net";
            if (checkPathExists(path)) {
                return path;
            }
            path = pattern + ".dk";
            if (checkPathExists(path)) {
                return path;
            }

        }
        return null;
    }

    private boolean checkPathExists(String path) {
        try {
            URI uri = new URI(path);
            URL url = uri.toURL();
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            huc.setConnectTimeout(2000);
            huc.connect();
            int responseCode = huc.getResponseCode();
            huc.disconnect();
            return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException | URISyntaxException e) {
            return false; // If there's an error, treat as not found
        }
    }
}
