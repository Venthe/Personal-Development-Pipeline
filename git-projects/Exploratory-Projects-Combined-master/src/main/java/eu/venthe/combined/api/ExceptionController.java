package eu.venthe.combined.api;

import eu.venthe.combined.exception.DomainException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception-manager")
public class ExceptionController {

    @GetMapping("/error")
    public void getError() {
        throw new DomainException("generic.message.error");
    }

    @GetMapping("/test")
    public void getTest() {
        throw new DomainException("generic.message.test");
    }
}
