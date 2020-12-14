package eu.venthe.combined.postgres;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Set;

public interface SampleEntityRepository extends CrudRepository<SampleEntity, BigInteger> {
    @Query(nativeQuery = true)
    Set<SampleEntity.Dto> getEntities();
}
