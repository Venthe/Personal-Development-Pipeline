package eu.venthe.pipeline.orchestrator.drivers.gerrit;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;

@RequiredArgsConstructor
@Component
public class GerritHeaders {
    private final GerritBindingConfiguration configuration;

    @NotNull
    public HttpHeaders prepareHeaders(String traceId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("X-Gerrit-Trace", of(traceId));
        httpHeaders.put("Authorization", of(getBasicAuth(configuration.getUsername() + ":" + configuration.getPassword())));
        return httpHeaders;
    }

    @NotNull
    public static String getBasicAuth(String value) {
        return "Basic " + base64Encode(value);
    }

    private static String base64Encode(String value) {
        return Base64.getEncoder()
                .encodeToString(value.getBytes(UTF_8));
    }
}
