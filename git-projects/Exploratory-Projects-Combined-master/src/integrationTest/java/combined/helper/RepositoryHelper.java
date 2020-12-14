package eu.venthe.combined.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public
class RepositoryHelper {
    private final List<CrudRepository<?, ?>> repositories;

    public void clearAll() {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
