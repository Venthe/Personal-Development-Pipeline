package eu.venthe.combined.postgres;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
        properties = {
                "spring.jpa.show-sql=true",
                "spring.jpa.properties.hibernate.format_sql=true",
                "logging.level.org.hibernate.SQL=DEBUG",
                "logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG",
                "logging.level.org.springframework.jdbc.core.StatementCreatorUtils=TRACE",
                "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE"
        }
)
class SampleEntityTest {
    @Autowired
    private SampleEntityRepository repository;

    @BeforeEach
    void clearAll() {
        repository.deleteAll();
    }

    @Test
    void test() {
        // Given
        final SampleEntity entity = new SampleEntity();
        entity.setName("Name");
        entity.setSurname("Surname");
        entity.setNested(new SampleEntity.SampleNested("nested", "nested2", new SampleEntity.SampleNested.SampleComplexType("value")));
        repository.save(entity);

        // When
        final Iterable<SampleEntity> all = repository.findAll();

        // Then
        Assertions.assertThat(all.iterator().next()).isEqualTo(entity);
    }

    @Test
    void test2() {
        // Given
        final SampleEntity entity = new SampleEntity();
        entity.setName("Name");
        entity.setSurname("Surname");
        entity.setValue1(1);
        entity.setValue2(2);
        entity.setNested(new SampleEntity.SampleNested("nested", "nested2", new SampleEntity.SampleNested.SampleComplexType("value")));
        repository.save(entity);
        final SampleEntity entity2 = new SampleEntity();
        entity2.setName("Name");
        entity2.setSurname("Surname");
        entity2.setValue1(3);
        entity2.setValue2(4);
        entity2.setNested(new SampleEntity.SampleNested("nested", "nested2", new SampleEntity.SampleNested.SampleComplexType("value")));
        repository.save(entity2);

        // When
        log.info("When");
        final Set<SampleEntity.Dto> all = repository.getEntities();

        // Then
        log.info("Then");
        Assertions.assertThat(all).containsExactly(new SampleEntity.Dto("Name", "nested", "value", 10));
    }

}
