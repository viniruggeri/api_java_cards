package fiap.tds.infrastructure;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.Arrays;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class MyApiFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "api.key")
    String apiKey;

    @ConfigProperty(name = "api.key.mobile")
    String apiKeyMobile;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var apiRequestKey = requestContext.getHeaderString("X-API-Key");
        var apiKeys = new String[]{apiKey, apiKeyMobile};

        if (apiRequestKey == null
                || Arrays.stream(apiKeys).noneMatch(ak -> ak.equals(apiRequestKey))) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("API Key is missing or invalid")
                            .build()
            );
        }
    }
}
