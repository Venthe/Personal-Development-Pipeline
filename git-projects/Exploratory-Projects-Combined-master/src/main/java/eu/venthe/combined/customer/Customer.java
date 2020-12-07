package eu.venthe.combined.customer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Document
@Getter
@AllArgsConstructor(staticName = "of", onConstructor_ = @PersistenceConstructor)
public class Customer {
    public static final Long DEFAULT_VALUE = 0L;

    @Id
    final String id;

    @Setter
    Long value = DEFAULT_VALUE;
}
