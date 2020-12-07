package eu.venthe.combined.microservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class MicroserviceService {
    private final MicroserviceConfiguration microserviceConfiguration;
    private final RestTemplate restTemplate;

    public MicroserviceBodyDto getInformation(String parameter) {
        return restTemplate.getForEntity(MessageFormat.format("{0}/hello-world/{1}", microserviceConfiguration.getUri(), parameter), MicroserviceBodyDto.class).getBody();
    }
}
