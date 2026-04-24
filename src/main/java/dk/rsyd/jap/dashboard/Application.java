package dk.rsyd.jap.dashboard;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
            title = "jap-dashboard",
            version = "1",
            description = "Dashboard for JAP services",
            contact = @Contact(name = "Applikationer - Udvikling, Regional IT, Region Syddanmark", email = "Udvikling.IT@rsyd.dk", url = "http://www.rsyd.dk")

    ),
        tags = {
        @Tag(name = "API")
        }
)
// tag::main[]
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

}
// end::main[]
