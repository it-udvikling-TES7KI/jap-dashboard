package dk.rsyd.jap.dashboard.harbor.client;

import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.ClientFilter;
import io.micronaut.http.annotation.RequestFilter;
import jakarta.inject.Singleton;

@BasicAuth
@Singleton
@ClientFilter()
public class HarborClientFilter {

    private final HarborConfiguration configuration;

    public HarborClientFilter(HarborConfiguration configuration) {
        this.configuration = configuration;
    }

    @RequestFilter
    public void doFilter(MutableHttpRequest<?> request) {
        request.basicAuth(configuration.username(), configuration.password());
    }
}
