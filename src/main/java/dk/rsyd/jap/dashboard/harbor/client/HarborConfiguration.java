package dk.rsyd.jap.dashboard.harbor.client;


import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

@ConfigurationProperties(HarborConfiguration.PREFIX)
@Requires(property = HarborConfiguration.PREFIX)
public record HarborConfiguration(
    String username,
    String password,
    String project

) {
    public static final String PREFIX = "harbor";
}
