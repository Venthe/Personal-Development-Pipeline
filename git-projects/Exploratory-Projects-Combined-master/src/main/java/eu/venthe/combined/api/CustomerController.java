package eu.venthe.combined.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.venthe.combined.customer.Customer;
import eu.venthe.combined.customer.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customer-manager")
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<Object> getBaseHref() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(
                customerRepository.findAll().stream()
                        .map(CustomerDto::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping(path = "/customers")
    public ResponseEntity<Customer> getCustomer(@RequestBody CustomerDto customer) {
        return ResponseEntity.ok(customerRepository.insert(CustomerDto.toEntity(customer)));
    }

    @GetMapping(path = "/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable String id) {
        return ResponseEntity.of(customerRepository.findById(id).map(CustomerDto::toDto));
    }

    @Value
    @AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES), staticName = "of")
    static class CustomerDto {
        String id;
        Long value;

        public static Customer toEntity(CustomerDto customerDto) {
            return Customer.of(customerDto.getId(), customerDto.getValue());
        }

        public static CustomerDto toDto(Customer customer) {
            return CustomerDto.of(customer.getId(), customer.getValue());
        }
    }
}
