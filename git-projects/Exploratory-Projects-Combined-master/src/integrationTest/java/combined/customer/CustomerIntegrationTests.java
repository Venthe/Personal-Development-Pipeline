package eu.venthe.combined.customer;

import eu.venthe.combined.helper.RepositoryHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class CustomerIntegrationTests {
    @Autowired
    private RepositoryHelper repositoryHelper;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void clearAll() {
        repositoryHelper.clearAll();
    }

    @Test
    void clearAllClearsCustomers() {
        // Given
        final String id = UUID.randomUUID().toString();
        final Customer entity = new Customer(id);
        customerRepository.insert(entity);
        Assertions.assertThat(customerRepository.findById(id))
                .isNotEmpty()
                .hasValueSatisfying(customer -> {
                    Assertions.assertThat(customer.getValue()).isEqualTo(Customer.DEFAULT_VALUE);
                    Assertions.assertThat(customer.getId()).isEqualTo(id);
                });

        // When
        repositoryHelper.clearAll();

        // Then
        Assertions.assertThat(customerRepository.findAll()).isEmpty();
    }

}
