package dk.rsyd.jap.dashboard.nomad;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class NomadJob {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("Name")
    private String name;

    private String serviceLink;

    private String nomadLink;

    @JsonCreator
    public NomadJob(String ID, String Name) {
        this.id = ID;
        this.name = Name;
        this.nomadLink = "https://nomad.rsyd.net/ui/jobs/" + name + "@jap";
    }

    /**
     * The environment can be found as the last part of the Nomad Jobs names. e.g "jap-service-dashboard-staging"
     *
     * @return the env
     */
    public String findEnv() {
        String[] nameArr = name.split("-");
        return nameArr[nameArr.length - 1];
    }

    public String getNomadLink() {
        return nomadLink;
    }

    public void setNomadLink(String nomadLink) {
        this.nomadLink = nomadLink;
    }

    public String getServiceLink() {
        return serviceLink;
    }

    public void setServiceLink(String serviceLink) {
        this.serviceLink = serviceLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String ID) {
        this.id = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    @Override
    public String toString() {
        return "NomadProject{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
