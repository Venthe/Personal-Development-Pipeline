package eu.venthe.combined.api;

import eu.venthe.combined.microservice.MicroserviceBodyDto;
import eu.venthe.combined.microservice.MicroserviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/microservice-manager")
public class MicroserviceController {
    private final MicroserviceService microserviceService;

    @GetMapping("/get-information/{parameter}")
    public MicroserviceBodyDto getAllCustomers(@PathVariable String parameter) {
        return microserviceService.getInformation(parameter);
    }
}
