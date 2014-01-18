package aichallenge;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long>
{
    Game findById(Long id);
}
