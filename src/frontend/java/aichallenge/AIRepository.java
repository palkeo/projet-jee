package aichallenge;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface AIRepository extends CrudRepository<AI, Long>
{
    List<AI> findByGameId(long gameId);
    List<AI> findByGameIdAndPidginId(long gameId, long userId);
    List<AI> findByGameIdAndPidginIdNot(long gameId, long userId);
}
