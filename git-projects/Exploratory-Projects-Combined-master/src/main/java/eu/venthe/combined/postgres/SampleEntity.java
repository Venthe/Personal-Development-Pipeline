package eu.venthe.combined.postgres;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@ToString
@EqualsAndHashCode
@NamedNativeQuery(
        name = "SampleEntity.getEntities",
        query = "select name as name, " +
                "       nested as nested, " +
                "       complex_type as complex_type, " +
                "       SUM(value1 + value2) as sum " +
                "from sample_entity " +
                "group by name, nested, complex_type",
        resultSetMapping = "mapping"
)
@SqlResultSetMapping(
        name = "mapping",
        classes = {
                @ConstructorResult(
                        targetClass = SampleEntity.Dto.class,
                        columns = {
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "nested"),
                                @ColumnResult(name = "complex_type"),
                                @ColumnResult(name = "sum", type = Integer.class)
                        }
                )
        }
)
public class SampleEntity {
    @Id
    @GeneratedValue
    private BigInteger id;
    private String name;
    private String surname;
    private int value1;
    private int value2;
    @Embedded
    private SampleNested nested;

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Setter
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class SampleNested {
        private String nested;
        private String nested2;
        @Convert(converter = SampleComplexTypeConverter.class)
        private SampleComplexType complexType;

        @Value
        public static class SampleComplexType {
            String value;
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Dto {
        private String name;
        private SampleNested.SampleComplexType complexType;
        private SuperNested superNested;
        private int sum;

        public Dto(String name, String nested, String complexType, int sum) {
            this.name = name;
            this.complexType = new SampleNested.SampleComplexType(complexType);
            this.superNested = new SuperNested(nested);
            this.sum = sum;
        }

        @ToString
        @EqualsAndHashCode
        @AllArgsConstructor
        public static class SuperNested {
            private String nested;
        }
    }
}
